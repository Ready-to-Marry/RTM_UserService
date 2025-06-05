package ready_to_marry.userservice.notification.dto.response;

import lombok.*;

/**
 * 알림 이력 (목록) 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistoryResponse {
    // 알림이 생성된 일시 (ISO 8601 형식, 예: 2025-06-05T14:30:00)
    private String createdAt;

    // 알림 제목
    private String title;

    // 알림 메시지 내용
    private String message;

    // 알림과 연관된 금액 정보
    private String amount;

    // 알림과 연관된 계약 식별자
    private String contractId;

    // 알림 상태
    private String status;

    // 다음 페이지 조회 시 사용될 커서를 담아서 반환
    private String nextExclusiveStartKey;
}