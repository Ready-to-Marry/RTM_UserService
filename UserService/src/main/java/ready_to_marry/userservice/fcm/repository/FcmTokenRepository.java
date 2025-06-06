package ready_to_marry.userservice.fcm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.fcm.entity.FcmToken;

/**
 * FcmToken CRUD 및 조회용 레포지토리
 */
@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}