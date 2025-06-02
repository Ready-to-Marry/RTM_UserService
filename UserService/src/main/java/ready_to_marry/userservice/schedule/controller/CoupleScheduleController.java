package ready_to_marry.userservice.schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.schedule.dto.request.ScheduleCreateRequest;
import ready_to_marry.userservice.schedule.dto.request.ScheduleUpdateRequest;
import ready_to_marry.userservice.schedule.dto.response.CoupleScheduleSummaryResponse;
import ready_to_marry.userservice.schedule.service.CoupleScheduleService;

import java.time.YearMonth;
import java.util.List;

/**
 * 유저의 커플 일정 관련 기능을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me/schedules")
@RequiredArgsConstructor
public class CoupleScheduleController {
    private final CoupleScheduleService coupleScheduleService;

    /**
     * 커플 일정 요약 목록 조회
     *
     * @param userId     게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param yearMonth  조회할 연월 (yyyy-MM 형식)
     * @return 성공 시 code=0, data=해당 연월의 커플 일정 요약 목록 정보
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<CoupleScheduleSummaryResponse>>> getMonthlyScheduleSummary(@RequestHeader("X-User-Id") Long userId, @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        List<CoupleScheduleSummaryResponse> schedules = coupleScheduleService.getMonthlyScheduleSummary(userId, yearMonth);

        ApiResponse<List<CoupleScheduleSummaryResponse>> response = ApiResponse.<List<CoupleScheduleSummaryResponse>>builder()
                .code(0)
                .message("Schedule list summary retrieved successfully")
                .data(schedules)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 일정 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSchedule(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid ScheduleCreateRequest request) {
        coupleScheduleService.createSchedule(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Schedule created successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 일정 수정
     */
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(@RequestHeader("X-User-Id") Long userId, @PathVariable("scheduleId") Long scheduleId, @RequestBody ScheduleUpdateRequest request) {
        coupleScheduleService.updateSchedule(userId, scheduleId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Schedule updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 일정 삭제
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@RequestHeader("X-User-Id") Long userId, @PathVariable Long scheduleId) {
        coupleScheduleService.deleteSchedule(userId, scheduleId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Schedule deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
