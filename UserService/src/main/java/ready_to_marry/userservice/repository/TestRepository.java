package ready_to_marry.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.entity.user.Users;

@Repository
public interface TestRepository extends JpaRepository<Users, Long> {
}
