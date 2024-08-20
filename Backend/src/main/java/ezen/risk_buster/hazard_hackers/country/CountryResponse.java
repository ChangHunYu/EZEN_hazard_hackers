package ezen.risk_buster.hazard_hackers.country;

public record CountryResponse(
        Long id,
        String continentName,
//        Long alertLevel,
        String countryEngName,
        String countryIsoAlp2,
        String countryName,
        String flagDownloadUrl,
        String mapDownloadUrl
) {
        public static CountryResponse of(Country country) {
//                Long alertLevel = 0L;
//                if (country.getAlert() != null) {
//                        alertLevel = country.getAlert().getLevel();
//                }

                return new CountryResponse(
                        country.getId(),
                        country.getContinent().getContinentNm(),
//                        alertLevel,
                        country.getCountryEngName(),
                        country.getCountryIsoAlp2(),
                        country.getCountryName(),
                        country.getFlagDownloadUrl(),
                        country.getMapDownloadUrl());
        }
}
