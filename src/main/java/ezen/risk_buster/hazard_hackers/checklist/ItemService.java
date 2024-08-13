package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    private ChecklistRepository checklistRepository;

    public Item createItem(Long checklistId, String description) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        Item item = Item.builder()
                .checklist(checklist)
                .description(description)
                .build();

        return itemRepository.save(item);
    }

    public Optional<Item> getItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> getItemsByChecklistId(Long checklistId) {
        return itemRepository.findByChecklistId(checklistId);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}