package ezen.risk_buster.hazard_hackers.checklist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping
    public ChecklistDto createChecklist(@RequestParam Long userId, @RequestParam String title) {
        Checklist checklist = checklistService.createChecklist(userId, title);
        ChecklistDto checklistDto = new ChecklistDto(checklist.getId(), checklist.getUser().getId(), checklist
                .getTitle(), new ArrayList<>(), checklist.isDeleted());
        return checklistDto;
    }

    @GetMapping("/{checklistId}")
    public ChecklistDto getChecklist(@PathVariable Long checklistId) {
        return checklistService.getChecklist(checklistId);
    }

    @GetMapping("/user/{userId}")
    public List<ChecklistDto> getChecklistsByUserId(@PathVariable Long userId) {
        return checklistService.getChecklistsByUserId(userId);
    }

    @PutMapping("/{checklistId}")
    public ChecklistDto updateChecklist(@PathVariable Long checklistId, @Valid @RequestBody ChecklistUpdateDto request) {
        return checklistService.updateChecklist(checklistId, request);
    }

    @DeleteMapping("/{checklistId}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long checklistId) {
        checklistService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }
}
