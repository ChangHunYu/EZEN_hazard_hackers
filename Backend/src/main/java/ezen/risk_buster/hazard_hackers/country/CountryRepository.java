package ezen.risk_buster.hazard_hackers.country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByIdAndIsDeletedFalse(Long id);
    @Query("SELECT c FROM Country c WHERE c.countryEngName = :countryEngName AND c.isDeleted = false")
    Country findByCountryEngNameAndIsDeletedFalse(@Param("countryEngName") String countryEngName);
}
