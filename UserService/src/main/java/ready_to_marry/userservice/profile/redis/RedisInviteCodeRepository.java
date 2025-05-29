package ready_to_marry.userservice.profile.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisInviteCodeRepository implements InviteCodeRepository {
    private static final String KEY_PREFIX = "invite:code:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String code, Long userId, Duration ttl) {
        String key = generateKey(code);
        redisTemplate.opsForValue().set(key, String.valueOf(userId), ttl);
    }

    @Override
    public Optional<Long> findUserIdByCode(String code) {
        String key = generateKey(code);
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value).map(Long::valueOf);
    }

    @Override
    public void delete(String code) {
        String key = generateKey(code);
        redisTemplate.delete(key);
    }

    private String generateKey(String code) {
        return KEY_PREFIX + code;
    }
}