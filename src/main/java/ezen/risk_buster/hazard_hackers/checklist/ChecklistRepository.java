package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByUserId(Long userId);
    List<Checklist> findAllByUserId(Long userId);
    Checklist findByItinerary_Id(Long itineraryId);
    List<Checklist> findAllByUserEmailAndUserIsDeletedFalseAndIsDeletedFalse(String email);
}