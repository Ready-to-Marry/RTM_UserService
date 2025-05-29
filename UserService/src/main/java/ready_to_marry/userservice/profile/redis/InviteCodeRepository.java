package ready_to_marry.userservice.profile.redis;

import java.time.Duration;
import java.util.Optional;

/**
 * 초대코드 저장소 추상화 인터페이스
 */
public interface InviteCodeRepository {
    /**
     * 초대코드를 Redis에 저장
     *
     * @param code      초대 코드
     * @param userId    발급한 유저의 도메인 ID
     * @param ttl       초대 코드 유효 기간(Duration)
     */
    void save(String code, Long userId, Duration ttl);

    /**
     * 초대 코드로 발급자(userId) 조회
     *
     * @param code      초대 코드
     * @return          유저 도메인 ID (없으면 Optional.empty())
     */
    Optional<Long> findUserIdByCode(String code);

    /**
     * 초대 코드 삭제
     *
     * @param code      삭제할 초대 코드
     */
    void delete(String code);
}