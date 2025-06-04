package ready_to_marry.userservice.budget.dto.response;

import lombok.*;
import ready_to_marry.userservice.budget.enums.BudgetCategory;

import java.time.LocalDate;

/**
 * 로그인한 유저의 커플 지출 내역 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoupleBudgetDetailResponse {
    // 지출 내역 ID
    private Long budgetDetailId;

    // 지출 카테고리
    private BudgetCategory category;

    // 지출 금액
    private Long spentAmount;

    // 지출 날짜
    private LocalDate date;

    // 지출 내용
    private String content;
}
