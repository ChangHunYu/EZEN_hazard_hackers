package ezen.risk_buster.hazard_hackers.checklist;

public record ItemResponseDto(
        Long id,
        boolean isChecked,
        Long checklistId,
        String description
) {
}
