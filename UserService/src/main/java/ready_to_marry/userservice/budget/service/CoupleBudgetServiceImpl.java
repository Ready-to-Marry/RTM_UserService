package ready_to_marry.userservice.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.budget.dto.request.BudgetDetailCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.entity.CoupleBudgetDetail;
import ready_to_marry.userservice.budget.entity.CoupleBudgetSummary;
import ready_to_marry.userservice.budget.enums.BudgetCategory;
import ready_to_marry.userservice.budget.repository.CoupleBudgetDetailRepository;
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
    private final CoupleBudgetDetailRepository coupleBudgetDetailRepository;
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

    @Override
    @Transactional
    public void createBudgetDetail(Long userId, BudgetDetailCreateRequest request) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 지출 내역 생성
        CoupleBudgetDetail detail = CoupleBudgetDetail.builder()
                .coupleId(coupleId)
                .category(request.getCategory())
                .spentAmount(request.getSpentAmount())
                .date(request.getDate())
                .content(request.getContent())
                .build();

        // 3) 지출 내역 저장
        try {
            coupleBudgetDetailRepository.save(detail);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }

        // 4) 해당 커플 ID의 커플 지출 요약 내역이 이미 존재하는지 검증
        CoupleBudgetSummary summary;
        try {
            Optional<CoupleBudgetSummary> optional = coupleBudgetSummaryRepository.findByCoupleId(coupleId);

            Long spent = request.getSpentAmount();
            BudgetCategory category = request.getCategory();

            // 4-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
            if (optional.isPresent()) {
                summary = optional.get();

                // 총 지출 금액 증가
                summary.setTotalSpent(summary.getTotalSpent() + spent);

                // 4-1-1) 총 예산이 이미 등록된 경우
                if (summary.getTotalBudget() != null) {
                    summary.setRemainingBudget(summary.getTotalBudget() - summary.getTotalSpent());
                } else { // 4-1-2) 총 예산이 등록되지 않은 경우
                    summary.setRemainingBudget(null);
                }

                // 카테고리별 총 지출 금액 증가
                switch (category) {
                    case HALL -> summary.setHallSpent(summary.getHallSpent() + spent);
                    case SDM -> summary.setSdmSpent(summary.getSdmSpent() + spent);
                    case CEREMONY -> summary.setCeremonySpent(summary.getCeremonySpent() + spent);
                    case SUPPLIES -> summary.setSuppliesSpent(summary.getSuppliesSpent() + spent);
                    case ETC -> summary.setEtcSpent(summary.getEtcSpent() + spent);
                }
            } else { // 4-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
                summary = CoupleBudgetSummary.builder()
                        .coupleId(coupleId)
                        .totalBudget(null)
                        .totalSpent(spent)
                        .remainingBudget(null)
                        .hallSpent(category == BudgetCategory.HALL ? spent : 0L)
                        .sdmSpent(category == BudgetCategory.SDM ? spent : 0L)
                        .ceremonySpent(category == BudgetCategory.CEREMONY ? spent : 0L)
                        .suppliesSpent(category == BudgetCategory.SUPPLIES ? spent : 0L)
                        .etcSpent(category == BudgetCategory.ETC ? spent : 0L)
                        .build();
            }
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 5) 지출 요약 내역 저장
        try {
            coupleBudgetSummaryRepository.save(summary);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }
}
