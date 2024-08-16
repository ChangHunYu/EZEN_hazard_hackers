package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.checklist.Checklist;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ItineraryResponse(
        Long id,
        String userEmail,
        String userCountryName,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        Long checklistId,
        String checklistTitle
) {

}
