package ezen.risk_buster.hazard_hackers.checklist;

import java.util.List;

public record ChecklistDto(
        Long id,
        Long userId,
        String title,
        List<ItemDto> items
) {
}
