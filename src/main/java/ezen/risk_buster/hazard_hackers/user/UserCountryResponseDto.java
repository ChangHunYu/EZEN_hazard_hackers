package ezen.risk_buster.hazard_hackers.user;

public record UserCountryResponseDto(
        Long id,
        String email,
        Long countryId,
        String countryName
) {
}
