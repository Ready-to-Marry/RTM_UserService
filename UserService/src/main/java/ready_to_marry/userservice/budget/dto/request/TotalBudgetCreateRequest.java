package ready_to_marry.userservice.budget.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 커플의 총 예산 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalBudgetCreateRequest {
    // 총 예산
    @NotNull
    @Min(0)
    private Long totalBudget;
}
