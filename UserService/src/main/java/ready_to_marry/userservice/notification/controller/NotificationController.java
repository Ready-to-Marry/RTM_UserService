package ready_to_marry.userservice.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.common.dto.response.Meta;
import ready_to_marry.userservice.notification.dto.request.DynamoPagingRequest;
import ready_to_marry.userservice.notification.dto.response.NotificationHistoryResponse;
import ready_to_marry.userservice.notification.service.NotificationHistoryService;

import java.util.List;

/**
 * 유저 알림 목록 조회 컨트롤러
 */
@RestController
@RequestMapping("/users/me/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationHistoryService notificationHistoryService;

    /**
     * 현재 로그인한 유저의 알림 목록 조회 (DynamoDB 커서 기반 페이징)
     *
     * @param userId        게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param pagingRequest DynamoDB 커서 기반 페이징 요청 정보
     * @return 성공 시 code=0, data=유저의 알림 목록 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationHistoryResponse>>> getMyNotifications(@RequestHeader("X-User-Id") String userId, @Valid @ModelAttribute DynamoPagingRequest pagingRequest) {
        Meta meta = new Meta();
        List<NotificationHistoryResponse> notifications = notificationHistoryService.getNotificationList(userId, pagingRequest, meta);

        ApiResponse<List<NotificationHistoryResponse>> response = ApiResponse.<List<NotificationHistoryResponse>>builder()
                .code(0)
                .message("Notification list retrieved successfully")
                .data(notifications)
                .meta(meta)
                .build();

        return ResponseEntity.ok(response);
    }
}