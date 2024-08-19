package ezen.risk_buster.hazard_hackers.itinerary;

import java.time.LocalDate;

public record ItineraryRequest(
//        User Id
        Long userCountryId,

        //컨트리 관련된 정보를 id로 받고있음
        Long countryId,
//        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {

}
