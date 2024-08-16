package ezen.risk_buster.hazard_hackers.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCountryRepostiory extends JpaRepository<UserCountry, Long> {
    List<UserCountry> findByUser_EmailAndIsDeletedFalse(String userEmail);
}
