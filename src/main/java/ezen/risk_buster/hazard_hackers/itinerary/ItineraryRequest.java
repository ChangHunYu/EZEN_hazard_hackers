package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.user.User;

import java.time.LocalDate;

public record ItineraryRequest(
//        User Id,
        Long userCountryId,
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
