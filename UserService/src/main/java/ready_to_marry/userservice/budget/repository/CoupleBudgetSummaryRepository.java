package ready_to_marry.userservice.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.userservice.budget.entity.CoupleBudgetSummary;

import java.util.Optional;
import java.util.UUID;

/**
 * CoupleBudgetSummary CRUD 및 조회용 레포지토리
 */
public interface CoupleBudgetSummaryRepository extends JpaRepository<CoupleBudgetSummary, Long> {
    /**
     * 특정 커플의 지출 요약 내역 조회
     *
     * @param coupleId 유저의 커플 ID
     * @return 커플 ID에 해당하는 CoupleBudgetSummary Optional
     */
    Optional<CoupleBudgetSummary> findByCoupleId(UUID coupleId);
}