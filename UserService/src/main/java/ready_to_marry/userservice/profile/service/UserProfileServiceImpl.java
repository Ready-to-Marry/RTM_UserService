package ready_to_marry.userservice.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.response.InternalProfileCreateResponse;
import ready_to_marry.userservice.profile.entity.UserProfile;
import ready_to_marry.userservice.profile.repository.UserProfileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;

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
}
