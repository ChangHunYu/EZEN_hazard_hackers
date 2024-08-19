package ezen.risk_buster.hazard_hackers.checklist;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChecklistDto(
        Long id,
        Long userId,
        Long itineraryId,
        String title,
        List<ItemDto> items,
        boolean deleted
) {
    public ChecklistDto(Long id, Long userId, String title, List<ItemDto> items, boolean isDeleted) {
        this(id, userId, null, title, items, isDeleted);
    }
}
