package ezen.risk_buster.hazard_hackers.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record CountryResponse(
        Long id,
        String countryEngName,
        String countryIsoAlp2,
        String countryName,
        String flagDownloadUrl,
        String mapDownloadUrl
) {
        public static CountryResponse of(Country country) {
                return new CountryResponse(
                        country.getId(),
                        country.getCountryEngName(),
                        country.getCountryIsoAlp2(),
                        country.getCountryName(),
                        country.getFlagDownloadUrl(),
                        country.getMapDownloadUrl());
        }
}
