package ready_to_marry.userservice.budget.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.budget.dto.request.BudgetDetailCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.service.CoupleBudgetService;
import ready_to_marry.userservice.common.dto.response.ApiResponse;

/**
 * 유저의 커플 지출 관련 기능을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me/budgets")
@RequiredArgsConstructor
public class CoupleBudgetController {
    private final CoupleBudgetService coupleBudgetService;

    /**
     * 커플 총 예산 등록
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request 총 예산 등록 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PostMapping("/total-budget")
    public ResponseEntity<ApiResponse<Void>> createTotalBudget(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody TotalBudgetCreateRequest request) {
        coupleBudgetService.createTotalBudget(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Total budget created successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 지출 내역 등록
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request 지출 내역 등록 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PostMapping("/details")
    public ResponseEntity<ApiResponse<Void>> createBudgetDetail(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody BudgetDetailCreateRequest request) {
        coupleBudgetService.createBudgetDetail(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Budget detail created successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
