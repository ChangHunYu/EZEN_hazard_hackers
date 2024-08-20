package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;

import java.util.Comparator;

public record CountryListResponse(
        Long id,
        String continentName,
        Long alertId,
        Long alertLevel,
        String countryEngName,
        String countryIsoAlp2,
        String countryName,
        String flagDownloadUrl,
        String mapDownloadUrl
) {
        public static CountryListResponse of(Country country) {
                Alert latestAlert = country.getAlertList().stream()
                        .max(Comparator.comparing(Alert::getWrittenDate)).orElse(null);


                return new CountryListResponse(
                        country.getId(),
                        country.getContinent().getContinentNm(),
                        latestAlert.getId(),
                        latestAlert.getLevel(),
                        country.getCountryEngName(),
                        country.getCountryIsoAlp2(),
                        country.getCountryName(),
                        country.getFlagDownloadUrl(),
                        country.getMapDownloadUrl());
        }
}
