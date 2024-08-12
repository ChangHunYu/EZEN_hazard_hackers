package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChecklistService {

    @Autowired
    private ChecklistRepository checklistRepository;

    public Checklist createChecklist(User user, String title) {
        Checklist checklist = new Checklist(user, title);

        return checklistRepository.save(checklist);
    }

    public Optional<Checklist> getChecklist(Long checklistId) {
        return checklistRepository.findById(checklistId);
    }

    public List<Checklist> getChecklistsByUserId(Long userId) {
        return checklistRepository.findByUserId(userId);
    }

    public void deleteChecklist(Long checklistId) {
        checklistRepository.deleteById(checklistId);
    }
}
