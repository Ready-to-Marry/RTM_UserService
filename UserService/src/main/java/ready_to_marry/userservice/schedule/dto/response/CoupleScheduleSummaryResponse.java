package ready_to_marry.userservice.schedule.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 로그인한 유저의 커플 일정 요약 목록 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoupleScheduleSummaryResponse {
    // 일정 ID
    private Long scheduleId;

    // 일정 제목
    private String title;

    // 일정 날짜
    private LocalDate date;

    // 일정 시각
    private LocalTime time;
}
