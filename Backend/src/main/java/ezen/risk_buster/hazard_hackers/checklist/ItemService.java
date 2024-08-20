package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    public ItemResponseDto create(ItemRequestDto request) {
        Checklist checklist = checklistRepository.findById(request.checklistId())
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        Item itemDto = Item.builder()
                .checklist(checklist)
                .description(request.description())
                .build();

        Item item = itemRepository.save(itemDto);

        return new ItemResponseDto(item.getId(), item.getIsChecked(), item.getChecklist().getId(), item.getDescription());
    }

    public ItemResponseDto getItem(String userEmail,Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElse(null);

        // 아이템이 존재하지 않는 경우 예외 처리
        if (item == null) {
            throw new RuntimeException("아이템을 찾을 수 없습니다: " + itemId);
        }

        // 로그인한 사용자와 아이템 소유자 확인
        // 예: 아이템의 소유자가 요청한 사용자와 동일한지 확인
        // 여기서는 예시로 item.getUser().getEmail()을 사용합니다.
        if (!item.getChecklist().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("사용자가 이 아이템에 접근할 권한이 없습니다.");
        }

        // 아이템 정보를 ItemResponseDto로 변환하여 반환
        return new ItemResponseDto(
                item.getId(),
                item.getIsChecked(),
                item.getChecklist().getId(),
                item.getDescription()
        );
    }

    public List<ItemResponseDto> getItemsByChecklistId(String userEmail, Long checklistId) throws AccessDeniedException {
        List<Item> items = itemRepository.findByChecklistId(checklistId);

        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("체크리스트를 찾을 수 없습니다 : " + checklistId));

        if (!checklist.getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("이 체크리스트에 접근할 권한이 없습니다: " + checklist );
        }


        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "ID가 있는 체크리스트 항목을 찾을 수 없습니다 : " + checklistId);
        }
        return items.stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getIsChecked(),
                        item.getChecklist().getId(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public void deleteItem(String userEmail , Long itemId) throws AccessDeniedException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID가 있는 아이템항목을 찾을 수가 없습니다 : " + itemId));

        if (!item.getChecklist().getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("이 아이템을 삭제 할 권한이 없습니다.");
        }
        itemRepository.delete(item);
    }



    public ItemResponseDto updateItem(
            String userEmail,Long itemId, String newDescription, Boolean newIsChecked) throws AccessDeniedException {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID가 있는 아이템항목을 찾을 수가 없습니다 : " + itemId));


        if (!item.getChecklist().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException(" 이 아이템을 수정할 권한이 없습니다.");
        }

        // 새로운 인스턴스를 생성하여 변경된 값 반영
        Item updatedItem = Item.builder()
                .id(item.getId())
                .checklist(item.getChecklist())
                .description(newDescription != null ? newDescription : item.getDescription())
                .isChecked(newIsChecked != null ? newIsChecked : item.getIsChecked())
                .build();

        updatedItem = itemRepository.save(updatedItem);

        return new ItemResponseDto(
                updatedItem.getId(),
                updatedItem.getIsChecked(),
                updatedItem.getChecklist().getId(),
                updatedItem.getDescription()
        );
    }


}

