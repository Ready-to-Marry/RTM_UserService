package ready_to_marry.userservice.budget.service;

import jakarta.persistence.EntityNotFoundException;
import ready_to_marry.userservice.budget.dto.request.BudgetDetailCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetCreateRequest;
import ready_to_marry.userservice.budget.dto.request.TotalBudgetUpdateRequest;
import ready_to_marry.userservice.budget.dto.response.CoupleBudgetSummaryResponse;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.ForbiddenException;
import ready_to_marry.userservice.common.exception.InfrastructureException;

/**
 * 커플 지출 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface CoupleBudgetService {
    /**
     * 유저 ID 기준으로 해당 커플의 총 예산 등록
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 커플 ID의 커플 지출 요약 내역이 이미 존재하는지 검증
     * 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
     * 2-1-1) 총 예산이 이미 등록된 경우
     * 2-1-2) 총 예산이 등록되지 않은 경우
     * 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
     * 3) 저장
     *
     * @param userId                    X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                   유저의 커플 총 예산 등록 요청 DTO
     * @throws EntityNotFoundException  본인의 프로필이 존재하지 않는 경우
     * @throws BusinessException        COUPLE_NOT_CONNECTED
     * @throws BusinessException        TOTAL_BUDGET_ALREADY_EXISTS
     * @throws InfrastructureException  DB_RETRIEVE_FAILURE
     * @throws InfrastructureException  DB_SAVE_FAILURE
     */
    void createTotalBudget(Long userId, TotalBudgetCreateRequest request);

    /**
     * 유저 ID 기준으로 해당 커플의 지출 내역 등록
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 지출 내역 생성
     * 3) 지출 내역 저장
     * 4) 해당 커플 ID의 커플 지출 요약 내역이 이미 존재하는지 검증
     * 4-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
     * 4-1-1) 총 예산이 이미 등록된 경우
     * 4-1-2) 총 예산이 등록되지 않은 경우
     * 4-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
     * 5) 지출 요약 내역 저장
     *
     * @param userId                    X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                   유저의 커플 지출 내역 등록 요청 DTO
     * @throws EntityNotFoundException  본인의 프로필이 존재하지 않는 경우
     * @throws BusinessException        COUPLE_NOT_CONNECTED
     * @throws InfrastructureException  DB_RETRIEVE_FAILURE
     * @throws InfrastructureException  DB_SAVE_FAILURE
     */
    void createBudgetDetail(Long userId, BudgetDetailCreateRequest request);

    /**
     * 지출 내역 ID 기준으로 특정 커플이 등록한 지출 내역 삭제
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 지출 내역 ID의 지출 내역이 존재하는지 검증
     * 3) 해당 지출 내역이 요청한 유저의 커플에 속해있는지 검증
     * 4) 삭제할 지출 내역의 정보 가져오기
     * 5) 지출 내역 삭제
     * 6) 커플 지출 요약 내역 갱신
     * 7) 지출 요약 내역 저장
     *
     * @param userId                    X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param budgetDetailId            삭제할 지출 내역 ID
     * @throws EntityNotFoundException  본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException  해당 budgetDetailId의 커플 지출 내역이 존재하지 않는 경우
     * @throws EntityNotFoundException  해당 coupleId의 커플 지출 요약 내역이 존재하지 않는 경우
     * @throws ForbiddenException       FORBIDDEN (해당 지출 내역이 요청한 유저의 커플 지출 내역이 아닌 경우)
     * @throws BusinessException        COUPLE_NOT_CONNECTED
     * @throws InfrastructureException  DB_RETRIEVE_FAILURE
     * @throws InfrastructureException  DB_DELETE_FAILURE
     * @throws InfrastructureException  DB_SAVE_FAILURE
     */
    void deleteBudgetDetail(Long userId, Long budgetDetailId);

    /**
     * 유저 ID 기준으로 해당 커플의 총 예산 수정
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 커플 ID의 커플 지출 요약 내역이 존재하고, 이미 등록한 총 예산이 존재하는지 검증
     * 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
     * 2-1-1) 총 예산이 이미 등록된 경우
     * 2-1-2) 총 예산이 등록되지 않은 경우
     * 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
     * 3) 저장
     *
     * @param userId                    X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                   유저의 커플 총 예산 수정 요청 DTO
     * @throws EntityNotFoundException  본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException  해당 coupleId의 커플 지출 요약 내역이 존재하지 않는 경우
     * @throws BusinessException        COUPLE_NOT_CONNECTED
     * @throws BusinessException        TOTAL_BUDGET_NOT_REGISTERED
     * @throws InfrastructureException  DB_RETRIEVE_FAILURE
     * @throws InfrastructureException  DB_SAVE_FAILURE
     */
    void updateTotalBudget(Long userId, TotalBudgetUpdateRequest request);

    /**
     * 유저 ID 기준으로 해당 커플의 총 예산 삭제
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 커플 ID의 커플 지출 요약 내역이 존재하고, 이미 등록한 총 예산이 존재하는지 검증
     * 2-1) 해당 커플 ID의 커플 지출 요약 내역이 존재하는 경우
     * 2-1-1) 총 예산이 이미 등록된 경우
     * 2-1-2) 총 예산이 등록되지 않은 경우
     * 2-2) 해당 커플 ID의 커플 지출 요약 내역이 존재하지 않은 경우
     * 3) 저장
     *
     * @param userId                    X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @throws EntityNotFoundException  본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException  해당 coupleId의 커플 지출 요약 내역이 존재하지 않는 경우
     * @throws BusinessException        COUPLE_NOT_CONNECTED
     * @throws BusinessException        TOTAL_BUDGET_NOT_REGISTERED
     * @throws InfrastructureException  DB_RETRIEVE_FAILURE
     * @throws InfrastructureException  DB_SAVE_FAILURE
     */
    void deleteTotalBudget(Long userId);

    /**
     * 유저 ID 기준으로 해당 커플의 지출 요약 내역 조회
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 커플의 지출 요약 내역 조회
     * 3) 조회된 커플 지출 요약 내역을 응답 DTO로 매핑하여 반환
     *
     * @param userId                        X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @return CoupleBudgetSummaryResponse  커플 지출 요약 내역 조회 결과 응답 DTO
     * @throws EntityNotFoundException      본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException      해당 coupleId의 커플 지출 요약 내역이 존재하지 않는 경우
     * @throws BusinessException            COUPLE_NOT_CONNECTED
     * @throws InfrastructureException      DB_RETRIEVE_FAILURE
     */
    CoupleBudgetSummaryResponse getBudgetSummary(Long userId);
}
