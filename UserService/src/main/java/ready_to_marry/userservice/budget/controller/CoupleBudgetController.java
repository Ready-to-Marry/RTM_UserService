package ready_to_marry.userservice.budget.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.budget.dto.request.BudgetDetailCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetUpdateRequest;
import ready_to_marry.userservice.budget.dto.response.CoupleBudgetDetailResponse;
import ready_to_marry.userservice.budget.dto.response.CoupleBudgetSummaryResponse;
import ready_to_marry.userservice.budget.service.CoupleBudgetService;
import ready_to_marry.userservice.common.dto.request.PagingRequest;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.common.dto.response.Meta;

import java.util.List;

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

    /**
     * 커플 지출 내역 삭제
     *
     * @param userId         게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param budgetDetailId 지출 내역 ID
     * @return 성공 시 code=0, data=null
     */
    @DeleteMapping("/details/{budgetDetailId}")
    public ResponseEntity<ApiResponse<Void>> deleteBudgetDetail(@RequestHeader("X-User-Id") Long userId, @PathVariable Long budgetDetailId) {
        coupleBudgetService.deleteBudgetDetail(userId, budgetDetailId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Budget detail deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 총 예산 수정
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request 총 예산 수정 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PatchMapping("/total-budget")
    public ResponseEntity<ApiResponse<Void>> updateTotalBudget(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid TotalBudgetUpdateRequest request) {
        coupleBudgetService.updateTotalBudget(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Total budget updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 총 예산 삭제
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=null
     */
    @DeleteMapping("/total-budget")
    public ResponseEntity<ApiResponse<Void>> deleteTotalBudget(@RequestHeader("X-User-Id") Long userId) {
        coupleBudgetService.deleteTotalBudget(userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Total budget deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 지출 요약 내역 조회
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=커플 지출 요약 내역 정보
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<CoupleBudgetSummaryResponse>> getBudgetSummary(@RequestHeader("X-User-Id") Long userId) {
        CoupleBudgetSummaryResponse budgetSummary = coupleBudgetService.getBudgetSummary(userId);

        ApiResponse<CoupleBudgetSummaryResponse> response = ApiResponse.<CoupleBudgetSummaryResponse>builder()
                .code(0)
                .message("Couple budget summary retrieved successfully")
                .data(budgetSummary)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 커플 지출 내역을 지출 날짜 기준으로 내림차순 페이징 조회
     *
     * @param userId        게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param pagingRequest 페이징 요청 정보 (page, size)
     * @return 성공 시 code=0, data=커플 지출 내역 페이징 결과 정보
     */
    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<CoupleBudgetDetailResponse>>> getBudgetDetailList(@RequestHeader("X-User-Id") Long userId, @ModelAttribute PagingRequest pagingRequest) {
        Page<CoupleBudgetDetailResponse> page = coupleBudgetService.getBudgetDetailList(userId, pagingRequest);

        ApiResponse<List<CoupleBudgetDetailResponse>> response = ApiResponse.<List<CoupleBudgetDetailResponse>>builder()
                .code(0)
                .message("Budget detail list retrieved successfully")
                .data(page.getContent())
                .meta(Meta.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
