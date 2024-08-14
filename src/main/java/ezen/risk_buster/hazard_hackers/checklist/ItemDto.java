package ezen.risk_buster.hazard_hackers.checklist;

import java.time.LocalDateTime;

public record ItemDto(
        Long id,
        boolean isChecked,
        boolean isDeleted,
        Long checklistId,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        LocalDateTime updatedAt,
        String description
) {
}
