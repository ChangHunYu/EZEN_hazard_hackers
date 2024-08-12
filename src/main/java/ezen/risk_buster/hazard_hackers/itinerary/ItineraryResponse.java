package ezen.risk_buster.hazard_hackers.itinerary;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ItineraryResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description

) {
}
