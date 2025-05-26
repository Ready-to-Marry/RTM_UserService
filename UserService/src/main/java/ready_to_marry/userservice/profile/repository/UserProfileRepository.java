package ready_to_marry.userservice.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.profile.entity.UserProfile;

/**
 * UserProfile CRUD 및 조회용 레포지토리
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}