package ezen.risk_buster.hazard_hackers.itinerary;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ItineraryResponse<R>(
        Long id,
        String userEmail,
        String userCountryName,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description

) {

}
