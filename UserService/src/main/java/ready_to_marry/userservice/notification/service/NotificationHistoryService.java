package ready_to_marry.userservice.notification.service;

import ready_to_marry.userservice.common.dto.response.Meta;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.notification.dto.request.DynamoPagingRequest;
import ready_to_marry.userservice.notification.dto.response.NotificationHistoryResponse;

import java.util.List;

/**
 * 유저 알림 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface NotificationHistoryService {
    /**
     * 특정 유저의 알림 목록을 DynamoDB 커서 기반 페이징으로 조회
     *
     * @param userId                                유저 도메인 ID
     * @param pagingRequest                         DynamoDB 커서 기반 페이징 요청 정보
     * @param metaOut                               조회 메타 정보
     * @return List<NotificationHistoryResponse>    알림 목록 (마지막 DTO에 nextExclusiveStartKey가 들어 있음)
     * @throws InfrastructureException              EXCLUSIVE_KEY_ENCODING_FAILURE
     * @throws InfrastructureException              EXCLUSIVE_KEY_DECODING_FAILURE
     * @throws InfrastructureException              DB_RETRIEVE_FAILURE
     */
    List<NotificationHistoryResponse> getNotificationList(String userId, DynamoPagingRequest pagingRequest, Meta metaOut);
}
