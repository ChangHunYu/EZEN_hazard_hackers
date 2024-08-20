package ezen.risk_buster.hazard_hackers.checklist;

public record ItemRequestDto(
        boolean isChecked,
        Long checklistId,
        String description
) {
}
