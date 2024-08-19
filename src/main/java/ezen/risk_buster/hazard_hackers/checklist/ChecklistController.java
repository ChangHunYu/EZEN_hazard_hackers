package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
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

    @PostMapping("/predefined")
    public ResponseEntity<ChecklistDto> createPredefinedChecklist(
            @RequestParam Long userId,
            @RequestParam CheckListType checkListType) {
        ChecklistDto createdChecklist = checklistService.createPredefinedChecklist(userId, checkListType);
        return ResponseEntity.ok(createdChecklist);
    }

    @PostMapping
    public ChecklistDto createChecklist(@RequestParam Long userId, @RequestParam String title) {
        Checklist checklist = checklistService.createChecklist(userId, title);
        ChecklistDto checklistDto = new ChecklistDto(checklist.getId(), checklist.getUser().getId(), checklist
                .getTitle(), new ArrayList<>(), checklist.isDeleted());
        return checklistDto;
    }

    @GetMapping("/{checklistId}")
    public ChecklistDto getChecklist(@LoginUser String userEmail, @PathVariable Long checklistId) {
        return checklistService.getChecklist(userEmail, checklistId);
    }

    @GetMapping
    public List<ChecklistDto> getChecklistsByUserId(@LoginUser String userEmail) {
        return checklistService.getChecklistsByUserId(userEmail);
    }

    @PutMapping("/{checklistId}")
    public ChecklistDto updateChecklist(@LoginUser String userEmail, @PathVariable Long checklistId, @Valid @RequestBody ChecklistUpdateDto request) {
        return checklistService.updateChecklist(userEmail, checklistId, request);
    }

    @DeleteMapping("/{checklistId}")
    public ResponseEntity<Void> deleteChecklist(@LoginUser String userEmail, @PathVariable Long checklistId) {
        checklistService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }
}
