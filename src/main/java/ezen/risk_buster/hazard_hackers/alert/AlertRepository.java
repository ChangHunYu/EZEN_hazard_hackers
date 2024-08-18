package ezen.risk_buster.hazard_hackers.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Alert findByIdAndIsDeletedFalse(Long id);
    @Query("SELECT a FROM Alert a JOIN a.country c WHERE c.countryEngName = :countryEngName")
    List<Alert> findAllByCountryEngName(@Param("countryEngName") String countryEngName);
}
