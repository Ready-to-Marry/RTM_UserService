package ready_to_marry.userservice.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.schedule.entity.CoupleSchedule;
import ready_to_marry.userservice.schedule.repository.projection.CoupleScheduleSummaryProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * CoupleSchedule CRUD 및 조회용 레포지토리
 */
@Repository
public interface CoupleScheduleRepository extends JpaRepository<CoupleSchedule, Long> {
    /**
     * 특정 커플의 특정 연월에 해당하는 일정 요약 목록 조회
     *
     * @param coupleId      커플 ID
     * @param startOfMonth  해당 월의 시작일 (YYYY-MM-01)
     * @param endOfMonth    해당 월의 말일 (YYYY-MM-마지막일)
     * @return 해당 기간의 커플 일정 요약 리스트 (조회 결과가 없으면 빈 리스트)
     */
    List<CoupleScheduleSummaryProjection> findAllByCoupleIdAndDateBetweenOrderByDateAscTimeAsc(
            UUID coupleId,
            LocalDate startOfMonth,
            LocalDate endOfMonth
    );
}
