package ready_to_marry.userservice.profile.service;

import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.response.InternalProfileCreateResponse;

/**
 * 유저 프로필 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface UserProfileService {
    /**
     * Internal API로 전달받은 최소 프로필 정보(name, phone)를 저장
     * 1) 전달받은 name, phone 값으로 UserProfile 엔티티 생성
     * 2) user_profile 테이블에 저장
     * 3) 생성된 userId를 포함한 응답 DTO 반환
     *
     * @param request name, phone 포함 요청 DTO
     * @return InternalProfileCreateResponse 생성된 userId 포함 응답 DTO
     * @throws InfrastructureException DB_SAVE_FAILURE
     */
    InternalProfileCreateResponse createInternalProfile(InternalProfileCreateRequest request);
}
