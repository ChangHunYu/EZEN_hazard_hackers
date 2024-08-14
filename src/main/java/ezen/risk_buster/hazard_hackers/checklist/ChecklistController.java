package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService checklistService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Checklist> createChecklist(@RequestParam Long userId, @RequestParam String title) {
        Checklist checklist = checklistService.createChecklist(userId, title);
        return ResponseEntity.ok(checklist);
    }


    @GetMapping("/{checklistId}")
    public ResponseEntity<Checklist> getChecklist(@PathVariable Long checklistId) {
        return checklistService.getChecklist(checklistId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Checklist>> getChecklistsByUserId(@PathVariable Long userId) {
        List<Checklist> checklists = checklistService.getChecklistsByUserId(userId);
        return ResponseEntity.ok(checklists);
    }

    @DeleteMapping("/{checklistId}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long checklistId) {
        checklistService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }
}