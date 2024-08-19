package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.country.Country;

public record UserCountryRequestDto(
        String email,
        Long countryId
) {
}
