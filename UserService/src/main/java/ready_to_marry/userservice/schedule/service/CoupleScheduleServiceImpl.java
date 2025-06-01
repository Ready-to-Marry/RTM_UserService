package ready_to_marry.userservice.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.profile.service.UserProfileService;
import ready_to_marry.userservice.schedule.dto.response.CoupleScheduleSummaryResponse;
import ready_to_marry.userservice.schedule.repository.CoupleScheduleRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleScheduleServiceImpl implements CoupleScheduleService {
    private final CoupleScheduleRepository coupleScheduleRepository;
    private final UserProfileService userProfileService;

    @Override
    @Transactional(readOnly = true)
    public List<CoupleScheduleSummaryResponse> getMonthlyScheduleSummary(Long userId, YearMonth yearMonth) {
        // 1) 유저(userId)로부터 커플 아이디 조회 (커플 미등록 시 예외 발생)
        UUID coupleId = userProfileService.getCoupleIdOrThrow(userId);

        // 2) 조회할 월의 시작일과 종료일 계산
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        // 3) 해당 커플의 일정 중 계산된 기간 사이에 있는 모든 일정 조회 후 정렬 및 DTO로 매핑
        try {
            return coupleScheduleRepository
                    .findAllByCoupleIdAndDateBetweenOrderByDateAscTimeAsc(coupleId, startOfMonth, endOfMonth)
                    .stream()
                    .map(proj -> CoupleScheduleSummaryResponse.builder()
                            .scheduleId(proj.getScheduleId())
                            .title(proj.getTitle())
                            .date(proj.getDate())
                            .time(proj.getTime())
                            .build())
                    .toList();
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=coupleId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskCoupleId(coupleId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }
    }
}
