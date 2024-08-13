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
    public void deleteItem(Long itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID가 있는 아이템항목을 찾을 수가 없습니다 : " + itemId));
        itemRepository.delete(item);
    }
}
