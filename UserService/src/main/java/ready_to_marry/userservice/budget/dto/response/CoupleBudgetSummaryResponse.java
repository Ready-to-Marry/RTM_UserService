package ready_to_marry.userservice.budget.dto.response;

import lombok.*;

/**
 * 로그인한 유저의 커플 지출 요약 내역 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoupleBudgetSummaryResponse {
    // 총 예산
    private Long totalBudget;

    // 총 지출 금액
    private Long totalSpent;

    // 남은 예산
    private Long remainingBudget;

    // 웨딩홀 총 지출 금액
    private Long hallSpent;

    // 스드메 총 지출 금액
    private Long sdmSpent;

    // 본식 총 지출 금액
    private Long ceremonySpent;

    // 혼수 총 지출 금액
    private Long suppliesSpent;

    // 기타 총 지출 금액
    private Long etcSpent;
}