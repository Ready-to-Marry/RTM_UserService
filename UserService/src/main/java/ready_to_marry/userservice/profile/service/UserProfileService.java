package ready_to_marry.userservice.profile.service;

import jakarta.persistence.EntityNotFoundException;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.exception.ValidationException;
import ready_to_marry.userservice.profile.dto.request.CoupleConnectRequest;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.request.ProfileUpdateRequest;
import ready_to_marry.userservice.profile.dto.response.InternalProfileCreateResponse;
import ready_to_marry.userservice.profile.dto.response.InviteCodeIssueResponse;

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

    /**
     * 현재 로그인한 유저의 프로필(name, phone, profileImage)을 수정
     * 전달된 name, phone, profileImage 중 null이 아닌 필드만 업데이트 (null-머지 방식)
     * 1) 유저 프로필 조회
     * 2) name 검증 및 수정
     * 3) phone 검증 및 수정
     * 4) profileImage 검증 및 업로드
     * 5) 저장 + 실패 시 S3 롤백 처리
     *
     * @param userId  X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request 수정 요청 DTO (name, phone, profileImage 중 일부 또는 전체 포함)
     * @throws EntityNotFoundException 유저 프로필이 존재하지 않는 경우
     * @throws ValidationException     name, phone, profileImage 검증에서 적절하지 값이 들어온 경우
     * @throws InfrastructureException DB_RETRIEVE_FAILURE
     * @throws InfrastructureException DB_SAVE_FAILURE
     * @throws InfrastructureException S3_UPLOAD_FAILURE
     */
    void updateMyProfile(Long userId, ProfileUpdateRequest request);

    /**
     * 현재 로그인한 유저의 커플 초대 코드 발급
     * 1) 중복되지 않는 초대코드 생성
     * 2) Redis에 저장
     * 3) 응답 반환
     *
     * @param userId  X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @return InviteCodeIssueResponse 발급된 초대 코드 응답 DTO
     * @throws InfrastructureException INVITE_CODE_GENERATION_FAILURE
     * @throws InfrastructureException INVITE_CODE_SAVE_FAILURE
     */
    InviteCodeIssueResponse issueInviteCode(Long userId);

    /**
     * 현재 로그인한 유저의 초대 코드 기반으로 커플 연결을 수행
     * 1) 초대 코드로 상대(발급자) userId 조회
     * 2) 자기 자신에게 연결 시도한 경우
     * 3) 유저 프로필 조회 (본인 + 상대방)
     * 4) 이미 커플 상태인지 확인
     * 5) coupleId 설정 및 저장
     * 6) 초대 코드 삭제
     *
     * @param userId  X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request 유저의 커플 연결 요청 DTO (inviteCode)
     * @throws EntityNotFoundException 본인 또는 상대방의 프로필이 존재하지 않는 경우
     * @throws BusinessException INVALID_INVITE_CODE
     * @throws BusinessException CANNOT_CONNECT_TO_SELF
     * @throws BusinessException ALREADY_CONNECTED_SELF
     * @throws BusinessException ALREADY_CONNECTED_PARTNER
     * @throws InfrastructureException DB_RETRIEVE_FAILURE
     * @throws InfrastructureException DB_SAVE_FAILURE
     * @throws InfrastructureException INVITE_CODE_RETRIEVE_FAILURE
     * @throws InfrastructureException INVITE_CODE_DELETE_FAILURE
     */
    void connectCouple(Long userId, CoupleConnectRequest request);
}