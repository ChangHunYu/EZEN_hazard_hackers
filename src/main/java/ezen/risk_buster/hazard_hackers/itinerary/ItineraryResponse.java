package ezen.risk_buster.hazard_hackers.itinerary;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ItineraryResponse(
        Long id,
        String userEmail,
        String userCountryEngName,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description

) {

}
