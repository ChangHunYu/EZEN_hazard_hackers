package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Checklist createChecklist(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Checklist checklist = Checklist.builder()
                .user(user)
                .title(title)
                .build();
        System.out.println("checklist = " + checklist);
        return checklistRepository.save(checklist);
    }

    public ChecklistDto getChecklist(Long checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElse(null);
        if (checklist == null) {
            throw new NoSuchElementException();
        }
        ChecklistDto checklistDto = new ChecklistDto(checklist.getId(),
                checklist.getUser().getId(),
                checklist.getTitle(),
                checklist.getItems()
                        .stream()
                        .map(item -> new ItemDto(
                                item.getId(),
                                item.getIsChecked(),
                                item.isDeleted(),
                                checklist.getId(),
                                item.getCreatedAt(),
                                item.getDeletedAt(),
                                item.getUpdatedAt(),
                                item.getDescription()))
                        .toList(),
                checklist.isDeleted());
        return checklistDto;
    }

    public List<ChecklistDto> getChecklistsByUserId(Long userId) {
        List<Checklist> checklists = checklistRepository.findAllByUserId(userId);
        List<ChecklistDto> checklistDtos = checklists.stream()
                .map(checklist -> new ChecklistDto(
                        checklist.getId(),
                        checklist.getUser().getId(),
                        checklist.getTitle(),
                        checklist.getItems()
                                .stream()
                                .map(item -> new ItemDto(
                                        item.getId(),
                                        item.getIsChecked(),
                                        item.isDeleted(),
                                        checklist.getId(),
                                        item.getCreatedAt(),
                                        item.getDeletedAt(),
                                        item.getUpdatedAt(),
                                        item.getDescription()))
                                .toList(),
                        checklist.isDeleted()
                )).toList();

        return checklistDtos;
    }

    @Transactional
    public ChecklistDto updateChecklist(Long checklistId, ChecklistUpdateDto request) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new NoSuchElementException("Checklist not found with id: " + checklistId));

        Checklist updatedChecklist = Checklist.builder()
                .id(checklist.getId())
                .user(checklist.getUser())
                .title(request.title())
                .items(createUpdatedItems(request.items(), checklist))
                .build();

        updatedChecklist = checklistRepository.save(updatedChecklist);

        return new ChecklistDto(
                updatedChecklist.getId(),
                updatedChecklist.getUser().getId(),
                updatedChecklist.getTitle(),
                updatedChecklist.getItems().stream()
                        .map(this::convertToItemDto)
                        .toList(),
                updatedChecklist.isDeleted()
        );
    }

    private List<Item> createUpdatedItems(List<ItemUpdateDto> itemUpdateDtos, Checklist checklist) {
        if (itemUpdateDtos == null) {
            return new ArrayList<>();
        }
        return itemUpdateDtos.stream()
                .map(itemDto -> Item.builder()
                        .description(itemDto.description())
                        .isChecked(itemDto.isChecked())
                        .checklist(checklist)
                        .build())
                .toList();
    }

    private ItemDto convertToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getIsChecked(),
                item.isDeleted(),
                item.getChecklist().getId(),
                item.getCreatedAt(),
                item.getDeletedAt(),
                item.getUpdatedAt(),
                item.getDescription()
        );
    }

    public void deleteChecklist(Long checklistId) {
        checklistRepository.deleteById(checklistId);
    }

}
