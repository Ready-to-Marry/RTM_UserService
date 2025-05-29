package ready_to_marry.userservice.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ready_to_marry.userservice.profile.config.InviteCodeProperties;
import ready_to_marry.userservice.profile.redis.InviteCodeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InviteCodeServiceImpl implements InviteCodeService {
    private final InviteCodeRepository inviteCodeRepository;
    private final InviteCodeProperties inviteCodeProperties;

    @Override
    @Retryable(
            include = DataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void save(String code, Long userId) {
        inviteCodeRepository.save(code, userId, inviteCodeProperties.getTtl());
    }

    @Override
    @Retryable(
            include = DataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public Optional<Long> getUserIdByInviteCode(String code) {
        return inviteCodeRepository.findUserIdByCode(code);
    }

    @Override
    @Retryable(
            include = DataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void delete(String code) {
        inviteCodeRepository.delete(code);
    }
}