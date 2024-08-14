package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByChecklistId(Long checklistId);
}

