package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ItemResponseDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "id에 해당하는 아이템이 없음: " + itemId));


        return new ItemResponseDto(
                item.getId(),
                item.getIsChecked(),
                item.getChecklist().getId(),
                item.getDescription()
        );
    }

    public List<ItemResponseDto> getItemsByChecklistId(Long checklistId) {
        List<Item> items = itemRepository.findByChecklistId(checklistId);

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

    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID가 있는 아이템항목을 찾을 수가 없습니다 : " + itemId));
        itemRepository.delete(item);
    }


    public ItemResponseDto updateItem(Long itemId) {
        // 이 예시에서는 updateDto 객체를 외부에서 주입받지 않고, 내부에서 생성합니다.
        // 이는 일반적인 사용 사례는 아니며, 주로 테스트나 특정 상황에서 사용됩니다.

        // 가정: 업데이트할 데이터가 하드코딩되어 있다고 가정합니다.
        String newDescription = "새로운 설명";
        Boolean newIsChecked = true;

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID가 있는 아이템항목을 찾을 수가 없습니다 : " + itemId));

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

