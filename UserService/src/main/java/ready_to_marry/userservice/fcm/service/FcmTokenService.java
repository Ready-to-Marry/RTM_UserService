package ready_to_marry.userservice.fcm.service;

import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.fcm.dto.request.FcmTokenCreateOrUpdateRequest;

/**
 * 유저 FCM 토큰 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface FcmTokenService {
    /**
     * 현재 로그인한 유저의 FCM 토큰을 등록하거나, 이미 존재하면 업데이트
     * 1) userId로 기존 FcmToken을 조회하거나 새 엔티티를 준비
     * 1-1) 해당 userId의 토큰이 존재하는 경우
     * 1-1-1) DB에 저장된 토큰과 요청으로 들어온 토큰이 다른 경우 → 업데이트
     * 1-1-2) DB에 저장된 토큰과 요청 토큰이 동일한 경우
     * 1-2) 해당 userId의 토큰이 존재하지 않는 경우 → 새로운 엔티티 생성
     * 2) 저장 혹은 업데이트
     *
     * @param userId            X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request           유저의 FCM 토큰 등록/업데이트 요청 DTO
     * @throws InfrastructureException DB_RETRIEVE_FAILURE
     * @throws InfrastructureException DB_SAVE_FAILURE
     */
    void saveOrUpdateToken(Long userId, FcmTokenCreateOrUpdateRequest request);
}