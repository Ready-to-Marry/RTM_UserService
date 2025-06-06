package ready_to_marry.userservice.budget.repository.projection;

import ready_to_marry.userservice.budget.enums.BudgetCategory;

import java.time.LocalDate;

/**
 * 커플 지출 내역 Projection (엔티티 대신 필드만 조회)
 */
public interface CoupleBudgetDetailProjection {
    // 지출 내역 ID
    Long getBudgetDetailId();

    // 지출 카테고리
    BudgetCategory getCategory();

    // 지출 금액
    Long getSpentAmount();

    // 지출 날짜
    LocalDate getDate();

    // 지출 내용
    String getContent();
}