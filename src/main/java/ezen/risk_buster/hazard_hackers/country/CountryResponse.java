package ezen.risk_buster.hazard_hackers.country;

public record CountryResponse(
        Long id,
        String continentName,
        Long alertLevel,
        String countryEngName,
        String countryIsoAlp2,
        String countryName,
        String flagDownloadUrl,
        String mapDownloadUrl
) {
        public static CountryResponse of(Country country) {
                return new CountryResponse(
                        country.getId(),
                        country.getContinent().getContinentNm(),
                        country.getAlert().getLevel(),
                        country.getCountryEngName(),
                        country.getCountryIsoAlp2(),
                        country.getCountryName(),
                        country.getFlagDownloadUrl(),
                        country.getMapDownloadUrl());
        }
}
