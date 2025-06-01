package ready_to_marry.userservice.schedule.repository.projection;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 커플 일정 요약 Projection (엔티티 대신 필드만 조회)
 */
public interface CoupleScheduleSummaryProjection {
    // 일정 ID
    Long getScheduleId();

    // 일정 제목
    String getTitle();

    // 일정 날짜
    LocalDate getDate();

    // 일정 시각
    LocalTime getTime();
}
