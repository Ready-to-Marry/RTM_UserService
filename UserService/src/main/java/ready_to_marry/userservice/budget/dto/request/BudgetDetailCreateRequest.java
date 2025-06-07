package ready_to_marry.userservice.budget.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ready_to_marry.userservice.budget.enums.BudgetCategory;

import java.time.LocalDate;

/**
 * 커플의 지출 내역 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDetailCreateRequest {
    // 지출 카테고리 (HALL, SDM, CEREMONY, SUPPLIES, ETC)
    @NotNull
    private BudgetCategory category;

    // 지출 금액
    @NotNull
    @Min(0)
    private Long spentAmount;

    // 지출 날짜
    @NotNull
    private LocalDate date;

    // 지출 내용
    @NotBlank
    @Size(max = 500)
    private String content;
}