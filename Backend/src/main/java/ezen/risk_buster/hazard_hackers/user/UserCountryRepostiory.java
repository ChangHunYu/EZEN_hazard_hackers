package ezen.risk_buster.hazard_hackers.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface UserCountryRepostiory extends JpaRepository<UserCountry, Long> {
    List<UserCountry> findByUser_EmailAndIsDeletedFalse(String userEmail);
    UserCountry findByIdAndIsDeletedFalse(Long id);
   // 일정에 관심국가를 등록하기 위해 코드 작성
   Optional<UserCountry> findByUserAndCountry_Id(User user, Long countryId);
}
