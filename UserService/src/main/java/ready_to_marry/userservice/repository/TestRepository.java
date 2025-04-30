package ready_to_marry.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ready_to_marry.userservice.entity.user.Users;

public interface TestRepository extends JpaRepository<Users, Long> {
}
