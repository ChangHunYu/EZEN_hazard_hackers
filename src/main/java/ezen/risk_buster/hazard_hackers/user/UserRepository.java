package ezen.risk_buster.hazard_hackers.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndIsDeletedFalse(String email);
    User findByIdAndIsDeletedFalse(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
