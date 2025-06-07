package ready_to_marry.userservice.fcm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.fcm.dto.request.FcmTokenCreateOrUpdateRequest;
import ready_to_marry.userservice.fcm.entity.FcmToken;
import ready_to_marry.userservice.fcm.repository.FcmTokenRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    @Override
    @Transactional
    public void saveOrUpdateToken(Long userId, FcmTokenCreateOrUpdateRequest request) {
        // 1) userId로 기존 FcmToken을 조회하거나 새 엔티티를 준비
        FcmToken fcmToken;
        try {
            Optional<FcmToken> optional = fcmTokenRepository.findById(userId);

            String token = request.getToken();

            // 1-1) 해당 userId의 토큰이 존재하는 경우
            if (optional.isPresent()) {
                fcmToken = optional.get();

                // 1-1-1) DB에 저장된 토큰과 요청으로 들어온 토큰이 다른 경우 → 업데이트
                if (!fcmToken.getToken().equals(token)) {
                    fcmToken.setToken(token);
                } else { // 1-1-2) DB에 저장된 토큰과 요청 토큰이 동일한 경우
                    return;
                }
            } else { // 1-2) 해당 userId의 토큰이 존재하지 않는 경우 → 새로운 엔티티 생성
                fcmToken = FcmToken.builder()
                        .userId(userId)
                        .token(token)
                        .build();
            }
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) 저장 혹은 업데이트
        try {
            fcmTokenRepository.save(fcmToken);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_SAVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_SAVE_FAILURE, ex);
        }
    }

    @Override
    @Transactional
    public void deleteToken(Long userId) {
        // 1) userId로 기존 FcmToken을 조회
        FcmToken fcmToken;
        try {
            fcmToken = fcmTokenRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("Fcm token not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("Fcm token not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 2) 삭제
        try {
            fcmTokenRepository.delete(fcmToken);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_DELETE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_DELETE_FAILURE, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getInternalFcmToken(Long userId) {
        // 1) userId로 FcmToken 엔티티를 조회
        try {
            return fcmTokenRepository.findById(userId)
                    // 2) 조회된 토큰(String) 반환
                    .map(FcmToken::getToken)
                    .orElseThrow(() -> {
                        log.error("Fcm token not found: identifierType=userId, identifierValue={}", MaskingUtils.maskUserId(userId));
                        return new EntityNotFoundException("Fcm token not found");
                    });
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        // 1) userId로 저장된 FcmToken이 존재하는지 확인
        try {
            return fcmTokenRepository.existsById(userId);
        } catch (DataAccessException ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(userId), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }
    }
}