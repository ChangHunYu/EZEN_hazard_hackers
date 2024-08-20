package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    Itinerary findByIdAndIsDeletedFalse(Long id);
//    List<Itinerary> findAllByIsDeletedFalse();
    List<Itinerary> findAllByUserEmailAndUserIsDeletedFalseAndIsDeletedFalse(String email);

}
