package ready_to_marry.userservice.schedule.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 유저의 커플 일정 수정 요청 DTO
 *
 * title, content, date, time 중 존재하는 필드만 수정
 * 빈 값(null)인 경우 해당 필드는 수정하지 않음 (null 체크 기반 머지)
 * 별도 검증 로직은 서비스 내부에서 처리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleUpdateRequest {
    // 일정 제목 (선택 수정)
    private String title;

    // 일정 내용 (선택 수정)
    private String content;

    // 일정 날짜 (선택 수정)
    private LocalDate date;

    // 일정 시각 (선택 수정)
    private LocalTime time;
}
