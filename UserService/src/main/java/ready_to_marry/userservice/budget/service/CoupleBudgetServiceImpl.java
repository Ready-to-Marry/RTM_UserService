package ready_to_marry.userservice.budget.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.budget.dto.request.BudgetDetailCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetUpdateRequest;
import ready_to_marry.userservice.budget.dto.response.CoupleBudgetDetailResponse;
import ready_to_marry.userservice.budget.dto.response.CoupleBudgetSummaryResponse;
import ready_to_marry.userservice.budget.entity.CoupleBudgetDetail;
import ready_to_marry.userservice.budget.entity.CoupleBudgetSummary;
import ready_to_marry.userservice.budget.enums.BudgetCategory;
import ready_to_marry.userservice.budget.repository.CoupleBudgetDetailRepository;
import ready_to_marry.userservice.budget.repository.CoupleBudgetSummaryRepository;
import ready_to_marry.userservice.budget.repository.projection.CoupleBudgetDetailProjection;
import ready_to_marry.userservice.common.dto.request.PagingRequest;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.ForbiddenException;
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

    @Override
    @Transactional
    public void deleteBudgetDetail(Long userId, Long budgetDetailId) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 해당 지출 내역 ID의 지출 내역이 존재하는지 검증
        CoupleBudgetDetail detail;
        try {
            detail = coupleBudgetDetailRepository.findById(budgetDetailId)
                    .orElseThrow(() -> {
                        log.error("Couple budget detail not found: identifierType=budgetDetailId, identifierValue={}", budgetDetailId);
                        return new EntityNotFoundException("Couple budget detail not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=budgetDetailId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), budgetDetailId, ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 3) 해당 지출 내역이 요청한 유저의 커플에 속해있는지 검증
        if (!detail.getCoupleId().equals(coupleId)) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.FORBIDDEN.getMessage(), MaskingUtils.maskCoupleId(coupleId));
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        // 4) 삭제할 지출 내역의 정보 가져오기
        Long spent = detail.getSpentAmount();
        BudgetCategory category = detail.getCategory();

        // 5) 지출 내역 삭제
        try {
            coupleBudgetDetailRepository.delete(detail);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=budgetDetailId, identifierValue={}", ErrorCode.DB_DELETE_FAILURE.getMessage(), budgetDetailId, ex);
            throw new InfrastructureException(ErrorCode.DB_DELETE_FAILURE, ex);
        }

        // 6) 커플 지출 요약 내역 갱신
        CoupleBudgetSummary summary;
        try {
            summary = coupleBudgetSummaryRepository.findByCoupleId(coupleId)
                    .orElseThrow(() -> {
                        log.error("Couple budget summary not found: identifierType=coupleId, identifierValue={}", MaskingUtils.maskCoupleId(coupleId));
                        return new EntityNotFoundException("Couple budget summary not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 총 지출 금액 갱신
        summary.setTotalSpent(summary.getTotalSpent() - spent);

        // 총 예산이 있는 경우 남은 예산 갱신
        if (summary.getTotalBudget() != null) {
            summary.setRemainingBudget(summary.getTotalBudget() - summary.getTotalSpent());
        }

        // 카테고리별 총 지출 금액 갱신
        switch (category) {
            case HALL -> summary.setHallSpent(summary.getHallSpent() - spent);
            case SDM -> summary.setSdmSpent(summary.getSdmSpent() - spent);
            case CEREMONY -> summary.setCeremonySpent(summary.getCeremonySpent() - spent);
            case SUPPLIES -> summary.setSuppliesSpent(summary.getSuppliesSpent() - spent);
            case ETC -> summary.setEtcSpent(summary.getEtcSpent() - spent);
        }

        // 7) 지출 요약 내역 저장
        try {
            coupleBudgetSummaryRepository.save(summary);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }

    @Override
    @Transactional
    public void updateTotalBudget(Long userId, TotalBudgetUpdateRequest request) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        Long newBudget = request.getTotalBudget();

        // 2) 해당 커플 ID의 커플 지출 요약 내역이 존재하고, 이미 등록한 총 예산이 존재하는지 검증
        CoupleBudgetSummary summary;
        try {
            Optional<CoupleBudgetSummary> optional = coupleBudgetSummaryRepository.findByCoupleId(coupleId);

            // 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
            if (optional.isPresent()) {
                summary = optional.get();

                // 2-1-1) 총 예산이 이미 등록된 경우
                if (summary.getTotalBudget() != null) {
                    summary.setTotalBudget(newBudget);
                    summary.setRemainingBudget(newBudget - summary.getTotalSpent());
                } else { // 2-1-2) 총 예산이 등록되지 않은 경우
                    log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.TOTAL_BUDGET_NOT_REGISTERED.getMessage(), MaskingUtils.maskCoupleId(coupleId));
                    throw new BusinessException(ErrorCode.TOTAL_BUDGET_NOT_REGISTERED);
                }
            } else { // 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
                log.error("Couple budget summary not found: identifierType=coupleId, identifierValue={}", MaskingUtils.maskCoupleId(coupleId));
                throw new EntityNotFoundException("Couple budget summary not found");
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
    public void deleteTotalBudget(Long userId) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 해당 커플 ID의 커플 지출 요약 내역이 존재하고, 이미 등록한 총 예산이 존재하는지 검증
        CoupleBudgetSummary summary;
        try {
            Optional<CoupleBudgetSummary> optional = coupleBudgetSummaryRepository.findByCoupleId(coupleId);

            // 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
            if (optional.isPresent()) {
                summary = optional.get();

                // 2-1-1) 총 예산이 이미 등록된 경우
                if (summary.getTotalBudget() != null) {
                    summary.setTotalBudget(null);
                    summary.setRemainingBudget(null);
                } else { // 2-1-2) 총 예산이 등록되지 않은 경우
                    log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.TOTAL_BUDGET_NOT_REGISTERED.getMessage(), MaskingUtils.maskCoupleId(coupleId));
                    throw new BusinessException(ErrorCode.TOTAL_BUDGET_NOT_REGISTERED);
                }
            } else { // 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
                log.error("Couple budget summary not found: identifierType=coupleId, identifierValue={}", MaskingUtils.maskCoupleId(coupleId));
                throw new EntityNotFoundException("Couple budget summary not found");
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
    @Transactional(readOnly = true)
    public CoupleBudgetSummaryResponse getBudgetSummary(Long userId) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 해당 커플의 지출 요약 내역 조회
        CoupleBudgetSummary summary;
        try {
            summary = coupleBudgetSummaryRepository.findByCoupleId(coupleId)
                    .orElseThrow(() -> {
                        log.error("Couple budget summary not found: identifierType=coupleId, identifierValue={}", MaskingUtils.maskCoupleId(coupleId));
                        return new EntityNotFoundException("Couple budget summary not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 3) 조회된 커플 지출 요약 내역을 응답 DTO로 매핑하여 반환
        return CoupleBudgetSummaryResponse.builder()
                .totalBudget(summary.getTotalBudget())
                .totalSpent(summary.getTotalSpent())
                .remainingBudget(summary.getRemainingBudget())
                .hallSpent(summary.getHallSpent())
                .sdmSpent(summary.getSdmSpent())
                .ceremonySpent(summary.getCeremonySpent())
                .suppliesSpent(summary.getSuppliesSpent())
                .etcSpent(summary.getEtcSpent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CoupleBudgetDetailResponse> getBudgetDetailList(Long userId, PagingRequest pagingRequest) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 페이징 요청 정보 생성
        PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize());

        // 3) 커플 지출 내역을 지출 날짜 기준으로 내림차순 정렬하여 조회 (Projection 기반)
        Page<CoupleBudgetDetailProjection> page;
        try {
            page = coupleBudgetDetailRepository.findByCoupleIdOrderByDateDesc(coupleId, pageRequest);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 4) 조회된 Projection 데이터를 DTO로 매핑하여 반환
        return page.map(proj -> CoupleBudgetDetailResponse.builder()
                .budgetDetailId(proj.getBudgetDetailId())
                .category(proj.getCategory())
                .spentAmount(proj.getSpentAmount())
                .date(proj.getDate())
                .content(proj.getContent())
                .build()
        );
    }
}
