package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChecklistService {

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }


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

    public ChecklistDto getChecklist(String userEmail, Long checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElse(null);

        if (checklist == null) {
            throw new RuntimeException("체크리스트를 못찾았어"); // 체크리스트가 존재하지 않는 경우
        }

        // 로그인한 사용자와 체크리스트 소유자 확인
        if (!checklist.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("이메일이 같지 않다"); // 권한이 없는 경우
        }

        return new ChecklistDto(
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
        );
    }

    public List<ChecklistDto> getChecklistsByUserId(String userEmail, Long userId) {
        // 사용자 권한 검증
        User requestingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("요청한 사용자를 찾을 수 없습니다."));

        if (!requestingUser.getId().equals(userId)) {
            throw new RuntimeException("다른 사용자의 체크리스트에 접근할 권한이 없습니다.");
        }

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
    public ChecklistDto updateChecklist(String userEmail, Long checklistId, ChecklistUpdateDto request) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new NoSuchElementException("Checklist not found with id: " + checklistId));

        // 사용자 권한 검증
        if (!checklist.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You don't have permission to update this checklist");
        }

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
