package ready_to_marry.userservice.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ready_to_marry.userservice.common.dto.response.Meta;
import ready_to_marry.userservice.notification.dto.request.DynamoPagingRequest;
import ready_to_marry.userservice.notification.dto.response.NotificationHistoryResponse;
import ready_to_marry.userservice.notification.repository.NotificationHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;

    @Override
    public List<NotificationHistoryResponse> getNotificationList(String userId, DynamoPagingRequest pagingRequest, Meta metaOut) {
        // 1) 리포지토리에서 DynamoDB Query(커서 기반) 수행
        List<NotificationHistoryResponse> result = notificationHistoryRepository.findByUserId(userId, pagingRequest);

        // 2) 메타 정보 세팅
        metaOut.setPage(pagingRequest.getPage());
        metaOut.setSize(pagingRequest.getSize());

        // 3) DynamoDB는 totalElements/totalPages를 바로 알 수 없으므로, 필요 시 별도 통계 테이블을 만들거나 생략
        metaOut.setTotalElements(0);
        metaOut.setTotalPages(0);

        return result;
    }
}
