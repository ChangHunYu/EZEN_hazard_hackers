package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
import ezen.risk_buster.hazard_hackers.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {


    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> create(@RequestBody ItemRequestDto request) {
        ItemResponseDto itemResponseDto = itemService.create(request);
        return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@LoginUser String userEmail, @PathVariable Long itemId) {
       ItemResponseDto itemResponseDto = itemService.getItem(userEmail,itemId);
       return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping("/checklist/{checklistId}")
    public ResponseEntity<List<ItemResponseDto>> getItemsByChecklistId(@LoginUser String userEmail,@PathVariable Long checklistId) throws AccessDeniedException {
        List<ItemResponseDto> items = itemService.getItemsByChecklistId(userEmail, checklistId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@LoginUser String userEmail,@PathVariable Long itemId) throws AccessDeniedException {
        itemService.deleteItem(userEmail,itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@LoginUser String userEmail, @PathVariable Long itemId,@RequestBody ItemUpdateDto updateDto) throws AccessDeniedException {
        ItemResponseDto updatedItemResponseDto = itemService.updateItem(userEmail,itemId, updateDto.description(),updateDto.isChecked() );
        return ResponseEntity.ok(updatedItemResponseDto);
    }
}
