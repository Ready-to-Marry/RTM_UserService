package ready_to_marry.userservice.profile.service;

import jakarta.persistence.EntityNotFoundException;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.exception.ValidationException;
import ready_to_marry.userservice.profile.dto.request.CoupleConnectRequest;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.dto.request.ProfileUpdateRequest;
import ready_to_marry.userservice.profile.dto.response.InviteCodeIssueResponse;
import ready_to_marry.userservice.profile.dto.response.UserProfileResponse;

import java.util.UUID;

/**
 * 유저 프로필 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface UserProfileService {
    /**
     * Internal API로 전달받은 최소 프로필 정보(name, phone)를 저장 + 푸시 알림 허용 시에만 전달되는 FCM 토큰 등록
     * 1) 전달받은 name, phone 값으로 UserProfile 엔티티 생성
     * 2) user_profile 테이블에 저장
     * 3) 푸시 알림 허용 시에만 전달되는 FCM 토큰 등록
     * 4) 생성된 userId 반환
     *
     * @param request                           name, phone, fcmToken 포함 요청 DTO
     * @return Long                             생성된 userId
     * @throws InfrastructureException          DB_RETRIEVE_FAILURE
     * @throws InfrastructureException          DB_SAVE_FAILURE
     */
    Long createInternalProfile(InternalProfileCreateRequest request);

    /**
     * 로그인한 유저의 프로필 정보 조회
     * 1) 유저 프로필 조회
     * 2) FCM 토큰 존재 여부로 푸시 허용 여부 판단
     * 3) 실명(표시명), 연락처, 프로필 사진 저장 주소, 커플 연결 여부, 유저 푸시 알림 허용 여부를 포함한 응답 DTO 반환
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @return UserProfileResponse          프로필 조회 결과 응답 DTO (유저 프로필 정보 및 커플 연결 여부 및 유저 푸시 알림 허용 여부 포함)
     * @throws EntityNotFoundException      유저 프로필이 존재하지 않는 경우
     * @throws InfrastructureException      DB_RETRIEVE_FAILURE
     */
    UserProfileResponse getMyProfile(Long userId);


    /**
     * 현재 로그인한 유저의 프로필(name, phone, profileImage)을 수정
     * 전달된 name, phone, profileImage 중 null이 아닌 필드만 업데이트 (null-머지 방식)
     * 1) 유저 프로필 조회
     * 2) name 검증 및 수정
     * 3) phone 검증 및 수정
     * 4) profileImage 검증 및 업로드
     * 5) 저장 + 실패 시 S3 롤백 처리
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                       수정 요청 DTO (name, phone, profileImage 중 일부 또는 전체 포함)
     * @throws EntityNotFoundException      유저 프로필이 존재하지 않는 경우
     * @throws ValidationException          name, phone, profileImage 검증에서 적절하지 값이 들어온 경우
     * @throws InfrastructureException      DB_RETRIEVE_FAILURE
     * @throws InfrastructureException      DB_SAVE_FAILURE
     * @throws InfrastructureException      S3_UPLOAD_FAILURE
     */
    void updateMyProfile(Long userId, ProfileUpdateRequest request);

    /**
     * 현재 로그인한 유저의 커플 초대 코드 발급
     * 1) 중복되지 않는 초대코드 생성
     * 2) Redis에 저장
     * 3) 응답 반환
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @return InviteCodeIssueResponse      발급된 초대 코드 응답 DTO
     * @throws InfrastructureException      INVITE_CODE_GENERATION_FAILURE
     * @throws InfrastructureException      INVITE_CODE_SAVE_FAILURE
     */
    InviteCodeIssueResponse issueInviteCode(Long userId);

    /**
     * 초대 코드 기반으로 현재 로그인한 유저의 커플 연결 수행
     * 1) 초대 코드로 상대(발급자) userId 조회
     * 2) 자기 자신에게 연결 시도한 경우
     * 3) 유저 프로필 조회 (본인 + 상대방)
     * 4) 이미 커플 상태인지 확인
     * 5) coupleId 설정 및 저장
     * 6) 초대 코드 삭제
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                       유저의 커플 연결 요청 DTO (inviteCode)
     * @throws EntityNotFoundException      본인 또는 상대방의 프로필이 존재하지 않는 경우
     * @throws BusinessException            INVALID_INVITE_CODE
     * @throws BusinessException            CANNOT_CONNECT_TO_SELF
     * @throws BusinessException            ALREADY_CONNECTED_SELF
     * @throws BusinessException            ALREADY_CONNECTED_PARTNER
     * @throws InfrastructureException      DB_RETRIEVE_FAILURE
     * @throws InfrastructureException      DB_SAVE_FAILURE
     * @throws InfrastructureException      INVITE_CODE_RETRIEVE_FAILURE
     * @throws InfrastructureException      INVITE_CODE_DELETE_FAILURE
     */
    void connectCouple(Long userId, CoupleConnectRequest request);

    /**
     *현재 로그인한 유저의 커플 연결 해제
     * 1) userId로 현재 로그인한 유저의 UserProfile 조회
     * 2) 본인이 커플 상태(coupleId 존재)인지 확인
     * 3) 상대방 프로필 조회 (같은 coupleId를 가진 프로필 중 본인이 아닌 상대)
     * 4) 본인과 상대방의 coupleId를 null로 설정하여 커플 해제
     * 5) 두 프로필 모두 저장
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @throws EntityNotFoundException      본인 또는 상대방의 프로필이 존재하지 않는 경우
     * @throws BusinessException            ALREADY_RELEASED
     * @throws InfrastructureException      DB_RETRIEVE_FAILURE
     * @throws InfrastructureException      DB_SAVE_FAILURE
     */
    void releaseCouple(Long userId);

    /**
     * 현재 로그인한 유저의 커플 ID 조회
     * 1) 유저 프로필 조회
     * 2) 커플 ID 여부 확인
     *
     * @param userId                   X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @return UUID                    해당 유저의 커플 ID
     * @throws EntityNotFoundException 본인의 프로필이 존재하지 않는 경우
     * @throws BusinessException       COUPLE_NOT_CONNECTED
     * @throws InfrastructureException DB_RETRIEVE_FAILURE
     */
    UUID getCoupleIdOrThrow(Long userId);
}