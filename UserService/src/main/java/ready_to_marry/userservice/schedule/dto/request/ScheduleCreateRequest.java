package ready_to_marry.userservice.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 유저의 커플 일정 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCreateRequest {
    // 일정 제목
    @NotBlank
    @Size(max = 50)
    private String title;

    // 일정 내용
    @NotBlank
    @Size(max = 500)
    private String content;

    // 일정 날짜
    @NotNull
    private LocalDate date;

    // 일정 시각
    @NotNull
    private LocalTime time;
}