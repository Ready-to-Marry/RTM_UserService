package ready_to_marry.userservice.budget.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * user_db.couple_budget_summary 테이블 매핑 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "couple_budget_summary")
public class CoupleBudgetSummary {
    // 지출 요약 내역 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_summary_id", updatable = false, nullable = false)
    private Long budgetSummaryId;

    // 커플 ID (외래 키)
    @Column(name = "couple_id", nullable = false, unique = true)
    private UUID coupleId;

    // 총 예산 (default = 0)
    @Column(name = "total_budget", nullable = false)
    private Long totalBudget;

    // 총 지출 금액 (hall_spent + sdm_spent + ceremony_spent + supplies_spent + etc_spent)
    @Column(name = "total_spent", nullable = false)
    private Long totalSpent;

    // 남은 예산 (total_budget - total_spent)
    @Column(name = "remaining_budget", nullable = false)
    private Long remainingBudget;

    // 웨딩홀 총 지출 금액 (default = 0)
    @Column(name = "hall_spent", nullable = false)
    private Long hallSpent;

    // 스드메 총 지출 금액 (default = 0)
    @Column(name = "sdm_spent", nullable = false)
    private Long sdmSpent;

    // 본식 총 지출 금액 (default = 0)
    @Column(name = "ceremony_spent", nullable = false)
    private Long ceremonySpent;

    // 혼수 총 지출 금액 (default = 0)
    @Column(name = "supplies_spent", nullable = false)
    private Long suppliesSpent;

    // 기타 총 지출 금액 (default = 0)
    @Column(name = "etc_spent", nullable = false)
    private Long etcSpent;
}
