package ready_to_marry.userservice.profile.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.exception.ValidationException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.request.ProfileUpdateRequest;
import ready_to_marry.userservice.profile.dto.response.InternalProfileCreateResponse;
import ready_to_marry.userservice.profile.entity.UserProfile;
import ready_to_marry.userservice.profile.repository.UserProfileRepository;
import ready_to_marry.userservice.profile.util.S3Storage;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final S3Storage s3Storage;

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
}