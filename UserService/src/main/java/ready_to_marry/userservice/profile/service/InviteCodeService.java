package ready_to_marry.userservice.profile.service;

import java.util.Optional;

/**
 * 초대 코드 저장·조회·삭제 기능 제공
 */
public interface InviteCodeService {
    /**
     * 초대 코드를 Redis에 저장
     *
     * @param code      생성된 초대 코드
     * @param userId    초대 코드 발급자(user)의 도메인 ID
     */
    void save(String code, Long userId);

    /**
     * 초대 코드로 발급자(userId) 조회
     *
     * @param code      초대 코드
     * @return 발급자 도메인 ID (없으면 Optional.empty())
     */
    Optional<Long> getUserIdByInviteCode(String code);

    /**
     * 초대 코드 삭제
     *
     * @param code      삭제할 초대 코드
     */
    void delete(String code);
}
