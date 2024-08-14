package ezen.risk_buster.hazard_hackers.country;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByIdAndIsDeletedFalse(Long id);
}
