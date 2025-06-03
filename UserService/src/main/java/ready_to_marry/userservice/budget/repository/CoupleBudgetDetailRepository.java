package ready_to_marry.userservice.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.userservice.budget.entity.CoupleBudgetDetail;

/**
 * CoupleBudgetDetail CRUD 및 조회용 레포지토리
 */
public interface CoupleBudgetDetailRepository extends JpaRepository<CoupleBudgetDetail, Long> {

}
