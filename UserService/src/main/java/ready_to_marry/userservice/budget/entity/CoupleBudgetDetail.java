package ready_to_marry.userservice.budget.entity;

import jakarta.persistence.*;
import lombok.*;
import ready_to_marry.userservice.budget.enums.BudgetCategory;

import java.time.LocalDate;
import java.util.UUID;

/**
 * user_db.couple_budget_detail 테이블 매핑 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "couple_budget_detail")
public class CoupleBudgetDetail {
    // 지출 내역 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_detail_id", updatable = false, nullable = false)
    private Long budgetDetailId;

    // 커플 ID (외래 키)
    @Column(name = "couple_id", nullable = false)
    private UUID coupleId;

    // 지출 카테고리
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private BudgetCategory category;

    // 지출 금액
    @Column(name = "spent_amount", nullable = false)
    private Long spentAmount;

    // 지출 날짜
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // 지출 내용
    @Column(name = "content", nullable = false, length = 500)
    private String content;
}
