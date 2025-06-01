package ready_to_marry.userservice.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.profile.entity.UserProfile;

import java.util.List;
import java.util.UUID;

/**
 * UserProfile CRUD 및 조회용 레포지토리
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * 커플 ID로 유저 프로필 리스트 조회
     *
     * @param coupleId 유저의 커플 ID
     * @return Optional.empty()이면 미존재
     */
    List<UserProfile> findByCoupleId(UUID coupleId);
}