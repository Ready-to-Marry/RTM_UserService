package ready_to_marry.userservice.profile.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.exception.ValidationException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.profile.dto.request.CoupleConnectRequest;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.request.ProfileUpdateRequest;
import ready_to_marry.userservice.profile.dto.response.InternalProfileCreateResponse;
import ready_to_marry.userservice.profile.dto.response.InviteCodeIssueResponse;
import ready_to_marry.userservice.profile.dto.response.UserProfileResponse;
import ready_to_marry.userservice.profile.entity.UserProfile;
import ready_to_marry.userservice.profile.repository.UserProfileRepository;
import ready_to_marry.userservice.profile.util.S3Storage;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final S3Storage s3Storage;
    private final InviteCodeService inviteCodeService;

    @Override
    @Transactional
    public InternalProfileCreateResponse createInternalProfile(InternalProfileCreateRequest request) {
        // 1) 전달받은 name, phone 값으로 UserProfile 엔티티 생성
        UserProfile profile = UserProfile.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        UserProfile savedUserProfile;
        try {
            // 2) user_profile 테이블에 저장
            savedUserProfile = userProfileRepository.save(profile);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=phone, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskPhone(request.getPhone()), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }

        // 3) 생성된 userId를 포함한 응답 DTO 반환
        return InternalProfileCreateResponse.builder()
                .userId(savedUserProfile.getUserId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        // 1) 유저 프로필 조회
        UserProfile profile;
        try {
            profile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("User profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) 실명(표시명), 연락처, 프로필 사진 저장 주소, 커플 연결 여부를 포함한 응답 DTO 반환
        return UserProfileResponse.builder()
                .name(profile.getName())
                .phone(profile.getPhone())
                .profileImgUrl(profile.getProfileImgUrl())
                .connectedCouple(profile.getCoupleId() != null)
                .build();
    }

    @Override
    @Transactional
    public void updateMyProfile(Long userId, ProfileUpdateRequest request) {
        // 1) 유저 프로필 조회
        UserProfile profile;
        try {
            profile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("User profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) name 검증 및 수정
        String name = request.getName();
        if (name != null) {
            if (name.isBlank() || name.length() > 50) {
                throw new ValidationException("name", "must be 1~50 characters and not blank");
            }
            profile.setName(name);
        }

        // 3) phone 검증 및 수정
        String phone = request.getPhone();
        if (phone != null) {
            if (!phone.matches("^\\+?[0-9\\-]{1,20}$")) {
                throw new ValidationException("phone", "Invalid phone number format");
            }
            profile.setPhone(phone);
        }

        // 4) profileImage 검증 및 업로드
        MultipartFile imageFile = request.getProfileImage();
        String uploadedImageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ValidationException("profileImage", "Only image files are allowed");
            }

            // 새 이미지 업로드
            try {
                uploadedImageUrl = s3Storage.upload(imageFile, "user-profile-images");
            } catch (IOException ex) {
                log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.S3_UPLOAD_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
                throw new InfrastructureException(ErrorCode.S3_UPLOAD_FAILURE, ex);
            }

            // 기존 이미지 삭제 (업로드 성공 후)
            String oldImageUrl = profile.getProfileImgUrl();
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                try {
                    s3Storage.delete(oldImageUrl);
                } catch (Exception ex) {
                    log.warn("System error occurred while deleting old image: imageUrl={}", oldImageUrl, ex);
                }
            }

            // 새로운 이미지 URL 설정
            profile.setProfileImgUrl(uploadedImageUrl);
        }

        // 5) 저장 + 실패 시 S3 롤백 처리
        try {
            userProfileRepository.save(profile);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);

            // S3에 올렸던 이미지 삭제 (rollback)
            if (uploadedImageUrl != null) {
                try {
                    s3Storage.delete(uploadedImageUrl);
                    log.info("Rolled back uploaded image from S3: imageUrl={}", uploadedImageUrl);
                } catch (Exception deleteEx) {
                    log.warn("System error occurred while deleting image from S3 after DB failure: imageUrl={}", uploadedImageUrl, deleteEx);
                }
            }

            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }

    @Override
    public InviteCodeIssueResponse issueInviteCode(Long userId) {
        // 1) 중복되지 않는 초대코드 생성
        String code = null;
        for (int i = 0; i < 5; i++) {
            String candidate = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            boolean exists = inviteCodeService.getUserIdByInviteCode(candidate).isPresent();
            if (!exists) {
                code = candidate;
                break;
            }
        }
        if (code == null) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.INVITE_CODE_GENERATION_FAILURE, MaskingUtils.maskUserId(userId));
            throw new InfrastructureException(ErrorCode.INVITE_CODE_GENERATION_FAILURE, null);
        }

        // 2) Redis에 저장
        try {
            inviteCodeService.save(code, userId);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.INVITE_CODE_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.INVITE_CODE_SAVE_FAILURE, ex);
        }

        // 3) 응답 반환
        return InviteCodeIssueResponse.builder()
                .inviteCode(code)
                .build();
    }

    @Override
    @Transactional
    public void connectCouple(Long userId, CoupleConnectRequest request) {
        String code = request.getInviteCode();

        // 1) 초대 코드로 상대(발급자) userId 조회
        Long targetUserId;
        try {
            targetUserId = inviteCodeService.getUserIdByInviteCode(code)
                    .orElseThrow(() -> {
                        log.error("{}: identifierType=inviteCode, identifierValue={}", ErrorCode.INVALID_INVITE_CODE.getMessage(), MaskingUtils.maskInviteCode(code));
                        return new BusinessException(ErrorCode.INVALID_INVITE_CODE);
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=inviteCode, identifierValue={}", ErrorCode.INVITE_CODE_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskInviteCode(code), ex);
            throw new InfrastructureException(ErrorCode.INVITE_CODE_RETRIEVE_FAILURE, ex);
        }

        // 2) 자기 자신에게 연결 시도한 경우
        if (userId.equals(targetUserId)) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.CANNOT_CONNECT_TO_SELF.getMessage(), MaskingUtils.maskUserId(userId));
            throw new BusinessException(ErrorCode.CANNOT_CONNECT_TO_SELF);
        }

        // 3) 유저 프로필 조회 (본인 + 상대방)
        UserProfile me;
        UserProfile partner;

        try {
            me = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("User profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        try {
            partner = userProfileRepository.findById(targetUserId)
                    .orElseThrow(() -> {
                        log.error("Target user profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(targetUserId));
                        return new EntityNotFoundException("Target user profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(targetUserId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 4) 이미 커플 상태인지 확인
        if (me.getCoupleId() != null) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.ALREADY_CONNECTED_SELF.getMessage(), MaskingUtils.maskUserId(userId));
            throw new BusinessException(ErrorCode.ALREADY_CONNECTED_SELF);
        }

        if (partner.getCoupleId() != null) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.ALREADY_CONNECTED_PARTNER.getMessage(), MaskingUtils.maskUserId(targetUserId));
            throw new BusinessException(ErrorCode.ALREADY_CONNECTED_PARTNER);
        }

        // 5) coupleId 설정 및 저장
        UUID coupleId = UUID.randomUUID();

        me.setCoupleId(coupleId);
        partner.setCoupleId(coupleId);

        try {
            userProfileRepository.save(me);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }

        try {
            userProfileRepository.save(partner);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(targetUserId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }

        // 6) 초대 코드 삭제
        try {
            inviteCodeService.delete(code);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=inviteCode, identifierValue={}", ErrorCode.INVITE_CODE_DELETE_FAILURE.getMessage(), MaskingUtils.maskInviteCode(code), ex);
            throw new InfrastructureException(ErrorCode.INVITE_CODE_DELETE_FAILURE, ex);
        }
    }

    @Override
    @Transactional
    public void releaseCouple(Long userId) {
        // 1) userId로 현재 로그인한 유저의 UserProfile 조회
        UserProfile me;
        try {
            me = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("User profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) 본인이 커플 상태(coupleId 존재)인지 확인
        UUID coupleId = me.getCoupleId();
        if (coupleId == null) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.ALREADY_RELEASED.getMessage(), MaskingUtils.maskUserId(userId));
            throw new BusinessException(ErrorCode.ALREADY_RELEASED);
        }

        // 3) 상대방 프로필 조회 (같은 coupleId를 가진 프로필 중 본인이 아닌 상대)
        UserProfile partner;
        try {
            partner = userProfileRepository.findByCoupleId(coupleId).stream()
                    .filter(profile -> !profile.getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Target user profile not found: identifierType=coupleId, identifierValue={}", MaskingUtils.maskCoupleId(coupleId));
                        return new EntityNotFoundException("Target user profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 4) 본인과 상대방의 coupleId를 null로 설정하여 커플 해제
        me.setCoupleId(null);
        partner.setCoupleId(null);

        // 5) 두 프로필 모두 저장
        try {
            userProfileRepository.save(me);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }

        try {
            userProfileRepository.save(partner);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(partner.getUserId()), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getCoupleIdOrThrow(Long userId) {
        // 1) 유저 프로필 조회
        UserProfile profile;
        try {
            profile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User profile not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("User profile not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) 커플 ID 여부 확인
        UUID coupleId = profile.getCoupleId();
        if (coupleId == null) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.COUPLE_NOT_CONNECTED.getMessage(), MaskingUtils.maskUserId(userId));
            throw new BusinessException(ErrorCode.COUPLE_NOT_CONNECTED);
        }

        return coupleId;
    }
}