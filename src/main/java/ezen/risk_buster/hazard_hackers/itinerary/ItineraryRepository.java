package ezen.risk_buster.hazard_hackers.itinerary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findAll();
    Itinerary findByIdAndIsDeletedFalse(Long id);
}
