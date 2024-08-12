package ezen.risk_buster.hazard_hackers.checklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {


    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestParam Long checklistId, @RequestParam String description) {
        Item item = itemService.createItem(checklistId, description);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/checklist/{checklistId}")
    public ResponseEntity<List<Item>> getItemsByChecklistId(@PathVariable Long checklistId) {
        List<Item> items = itemService.getItemsByChecklistId(checklistId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}

