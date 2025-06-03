package ready_to_marry.userservice.schedule.service;

import jakarta.persistence.EntityNotFoundException;
import ready_to_marry.userservice.common.exception.BusinessException;
import ready_to_marry.userservice.common.exception.ForbiddenException;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.exception.ValidationException;
import ready_to_marry.userservice.schedule.dto.request.ScheduleCreateRequest;
import ready_to_marry.userservice.schedule.dto.request.ScheduleUpdateRequest;
import ready_to_marry.userservice.schedule.dto.response.CoupleScheduleDetailResponse;
import ready_to_marry.userservice.schedule.dto.response.CoupleScheduleSummaryResponse;

import java.time.YearMonth;
import java.util.List;

/**
 * 커플 일정 도메인의 비즈니스 로직을 제공하는 서비스 인터페이스
 */
public interface CoupleScheduleService {
    /**
     * 유저 ID 기준으로 해당 커플의 특정 연월 일정 요약 조회
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 조회할 월의 시작일과 종료일 계산
     * 3) 해당 커플의 일정 중 계산된 기간 사이에 있는 모든 일정 조회 후 정렬 및 DTO로 매핑
     *
     * @param userId                                X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param yearMonth                             조회할 연월 (yyyy-MM)
     * @return List<CoupleScheduleSummaryResponse>  특정 연월에 해당하는 커플 일정 요약 리스트
     * @throws EntityNotFoundException              본인의 프로필이 존재하지 않는 경우
     * @throws BusinessException                    COUPLE_NOT_CONNECTED
     * @throws InfrastructureException              DB_RETRIEVE_FAILURE
     */
    List<CoupleScheduleSummaryResponse> getMonthlyScheduleSummary(Long userId, YearMonth yearMonth);

    /**
     * 유저 ID 기준으로 해당 커플의 일정 등록
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) CoupleSchedule 엔티티 생성
     * 3) 저장
     *
     * @param userId                               X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param request                              유저의 커플 일정 등록 요청 DTO
     * @throws EntityNotFoundException             본인의 프로필이 존재하지 않는 경우
     * @throws BusinessException                   COUPLE_NOT_CONNECTED
     * @throws InfrastructureException             DB_RETRIEVE_FAILURE
     * @throws InfrastructureException             DB_SAVE_FAILURE
     */
    void createSchedule(Long userId, ScheduleCreateRequest request);

    /**
     * 일정 ID 기준으로 특정 커플이 등록한 일정 수정
     * 전달된 title, content, date, time 중 null이 아닌 필드만 업데이트 (null-머지 방식)
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 일정 ID의 일정이 존재하는지 검증
     * 3) 해당 일정이 요청한 유저의 커플에 속해있는지 검증
     * 4) time이 null이 아니면 검증 및 수정
     * 5) content가 null이 아니면 검증 및 수정
     * 6) date가 null이 아니면 수정
     * 7) time이 null이 아니면 수정
     * 8) 저장
     *
     * @param userId                               X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param scheduleId                           수정할 일정 ID
     * @param request                              유저의 커플 일정 수정 요청 DTO
     * @throws EntityNotFoundException             본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException             해당 scheduleId의 커플 일정이 존재하지 않는 경우
     * @throws ForbiddenException                  FORBIDDEN (해당 일정이 요청한 유저의 커플 일정이 아닌 경우)
     * @throws ValidationException                 title, content 검증에서 적절하지 값이 들어온 경우
     * @throws BusinessException                   COUPLE_NOT_CONNECTED
     * @throws InfrastructureException             DB_RETRIEVE_FAILURE
     * @throws InfrastructureException             DB_SAVE_FAILURE
     */
    void updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request);

    /**
     * 일정 ID 기준으로 특정 커플이 등록한 일정 삭제
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 일정 ID의 일정이 존재하는지 검증
     * 3) 해당 일정이 요청한 유저의 커플에 속해있는지 검증
     * 4) 삭제
     *
     * @param userId                                X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param scheduleId                            삭제할 일정 ID
     * @throws EntityNotFoundException              본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException              해당 scheduleId의 커플 일정이 존재하지 않는 경우
     * @throws ForbiddenException                   FORBIDDEN (해당 일정이 요청한 유저의 커플 일정이 아닌 경우)
     * @throws BusinessException                    COUPLE_NOT_CONNECTED
     * @throws InfrastructureException              DB_RETRIEVE_FAILURE
     * @throws InfrastructureException              DB_DELETE_FAILURE
     */
    void deleteSchedule(Long userId, Long scheduleId);

    /**
     * 일정 ID 기준으로 특정 커플이 등록한 일정 상세 조회
     * 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
     * 2) 해당 일정 ID의 일정이 존재하는지 검증
     * 3) 해당 일정이 요청한 유저의 커플에 속해있는지 검증
     * 4) 해당 일정 DTO로 매핑
     *
     * @param userId                                X-User-Id 헤더에서 전달받은 유저 도메인 ID
     * @param scheduleId                            조회할 일정 ID
     * @return CoupleScheduleDetailResponse         커플 일정 상세 정보
     * @throws EntityNotFoundException              본인의 프로필이 존재하지 않는 경우
     * @throws EntityNotFoundException              해당 scheduleId의 커플 일정이 존재하지 않는 경우
     * @throws ForbiddenException                   FORBIDDEN (해당 일정이 요청한 유저의 커플 일정이 아닌 경우)
     * @throws BusinessException                    COUPLE_NOT_CONNECTED
     * @throws InfrastructureException              DB_RETRIEVE_FAILURE
     */
    CoupleScheduleDetailResponse getScheduleDetail(Long userId, Long scheduleId);
}
