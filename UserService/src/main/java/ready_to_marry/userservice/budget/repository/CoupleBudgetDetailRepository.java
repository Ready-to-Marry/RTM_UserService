package ready_to_marry.userservice.budget.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.userservice.budget.entity.CoupleBudgetDetail;
import ready_to_marry.userservice.budget.repository.projection.CoupleBudgetDetailProjection;

import java.util.UUID;

/**
 * CoupleBudgetDetail CRUD 및 조회용 레포지토리
 */
public interface CoupleBudgetDetailRepository extends JpaRepository<CoupleBudgetDetail, Long> {
    /**
     * 특정 커플의 지출 내역을 지출 날짜 내림차순으로 페이징 조회
     *
     * @param coupleId      커플 ID
     * @param pageable      페이징 정보
     * @return 해당 커플의 지출 내역을 지출 날짜 내림차순으로 정렬한 페이징 결과
     */
    Page<CoupleBudgetDetailProjection> findByCoupleIdOrderByDateDesc(UUID coupleId, Pageable pageable);
}
