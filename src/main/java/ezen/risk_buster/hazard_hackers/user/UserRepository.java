package ezen.risk_buster.hazard_hackers.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User deleteByEmail(String email);
}
