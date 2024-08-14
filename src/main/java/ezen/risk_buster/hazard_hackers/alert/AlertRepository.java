package ezen.risk_buster.hazard_hackers.alert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Alert findByIdAndIsDeletedFalse(Long id);
}
