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
    public ResponseEntity<ItemResponseDto> create(@RequestBody ItemRequestDto request) {
        ItemResponseDto itemResponseDto = itemService.create(request);
        return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long itemId) {
       ItemResponseDto itemResponseDto = itemService.getItem(itemId);
       return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping("/checklist/{checklistId}")
    public ResponseEntity<List<ItemResponseDto>> getItemsByChecklistId(@PathVariable Long checklistId) {
        List<ItemResponseDto> items = itemService.getItemsByChecklistId(checklistId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}

