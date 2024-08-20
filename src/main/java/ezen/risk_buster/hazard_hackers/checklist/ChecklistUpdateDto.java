package ezen.risk_buster.hazard_hackers.checklist;

import java.util.List;

public record ChecklistUpdateDto(String title,
                                 List<ItemUpdateDto> items
                                 ) {
}
