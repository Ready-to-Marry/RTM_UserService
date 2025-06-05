package ready_to_marry.userservice.notification.repository;

import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.notification.dto.request.DynamoPagingRequest;
import ready_to_marry.userservice.notification.dto.response.NotificationHistoryResponse;

import java.util.List;

public interface NotificationHistoryRepository {
    /**
     * 유저 ID 기준으로 알림 목록 조회 (DynamoDB 커서 기반 페이징)
     *
     * @param userId        유저 도메인 ID
     * @param pagingRequest DynamoDB 커서 기반 페이징 요청 정보
     * @return 알림 목록 응답
     * @throws InfrastructureException EXCLUSIVE_KEY_ENCODING_FAILURE
     * @throws InfrastructureException EXCLUSIVE_KEY_DECODING_FAILURE
     * @throws InfrastructureException DB_RETRIEVE_FAILURE
     */
    List<NotificationHistoryResponse> findByUserId(String userId, DynamoPagingRequest pagingRequest);
}