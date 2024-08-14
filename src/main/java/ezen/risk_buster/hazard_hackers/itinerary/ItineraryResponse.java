package ezen.risk_buster.hazard_hackers.itinerary;

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
        String description

) {

}
