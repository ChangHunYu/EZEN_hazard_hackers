package ezen.risk_buster.hazard_hackers.itinerary;

import java.time.LocalDate;

public record ItineraryRequest(
        Long userId,
        Long userCountryId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
