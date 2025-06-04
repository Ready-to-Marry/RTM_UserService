package ready_to_marry.userservice.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.entity.CoupleBudgetSummary;
import ready_to_marry.userservice.budget.repository.CoupleBudgetSummaryRepository;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.profile.service.UserProfileService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleBudgetServiceImpl implements CoupleBudgetService {
    private final CoupleBudgetSummaryRepository coupleBudgetSummaryRepository;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public void createTotalBudget(Long userId, TotalBudgetCreateRequest request) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 해당 커플 ID의 커플 지출 요약 내역이 이미 존재하는지 검증
        CoupleBudgetSummary summary;
        try {
            Optional<CoupleBudgetSummary> optional = coupleBudgetSummaryRepository.findByCoupleId(coupleId);

            // 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
            if (optional.isPresent()) {
                summary = optional.get();

                // 2-1-1) 총 예산이 이미 등록된 경우
                if (summary.getTotalBudget() != null) {
                    log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.TOTAL_BUDGET_ALREADY_EXISTS.getMessage(), MaskingUtils.maskCoupleId(coupleId));
                    throw new BusinessException(ErrorCode.TOTAL_BUDGET_ALREADY_EXISTS);
                } else { // 2-1-2) 총 예산이 등록되지 않은 경우
                    summary.setTotalBudget(request.getTotalBudget());
                    summary.setRemainingBudget(request.getTotalBudget() - summary.getTotalSpent());
                }
            } else { // 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
                summary = CoupleBudgetSummary.builder()
                        .coupleId(coupleId)
                        .totalBudget(request.getTotalBudget())
                        .totalSpent(0L)
                        .remainingBudget(request.getTotalBudget())
                        .hallSpent(0L)
                        .sdmSpent(0L)
                        .ceremonySpent(0L)
                        .suppliesSpent(0L)
                        .etcSpent(0L)
                        .build();
            }
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 3) 저장
        try {
            coupleBudgetSummaryRepository.save(summary);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }
}
