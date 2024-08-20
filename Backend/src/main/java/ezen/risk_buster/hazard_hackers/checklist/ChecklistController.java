package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
import ezen.risk_buster.hazard_hackers.itinerary.Itinerary;
import ezen.risk_buster.hazard_hackers.itinerary.ItineraryRepository;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    @Autowired
    private final ChecklistService checklistService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;

    @PostMapping("/predefined")
    public ResponseEntity<ChecklistDto> createPredefinedChecklist(
            @LoginUser String userEmail,
            @RequestParam CheckListType checkListType) {
        ChecklistDto createdChecklist = checklistService.createPredefinedChecklist(userEmail, checkListType);
        return ResponseEntity.ok(createdChecklist);
    }

    @PostMapping
    public ChecklistDto createChecklist(@LoginUser String userEmail, @RequestParam String title) {
        Checklist checklist = checklistService.createChecklist(userEmail, title);
        ChecklistDto checklistDto = new ChecklistDto(checklist.getId(), checklist.getUser().getId(), checklist
                .getTitle(), new ArrayList<>(), checklist.isDeleted());
        return checklistDto;
    }

    @GetMapping("/{checklistId}")
    public ChecklistDto getChecklist(@LoginUser String userEmail, @PathVariable Long checklistId) {
        return checklistService.getChecklist(userEmail, checklistId);
    }

    @GetMapping("/itinerary/{itineraryId}")
    public ResponseEntity<ChecklistDto> getChecklistByItineraryId(@LoginUser String userEmail, @PathVariable Long itineraryId) {
        ChecklistDto checklist = checklistService.getChecklistByItineraryId(userEmail, itineraryId);

        if (checklist == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(checklist);
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
