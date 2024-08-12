package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;
    private final AlertRepository alertRepository;

    public CountryService(CountryRepository countryRepository, ContinentRepository continentRepository, AlertRepository alertRepository) {
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
        this.alertRepository = alertRepository;
    }

    public CountryResponse create(CountryRequest request) {
        Continent continent = continentRepository.findById(request.continentId()).orElse(null);
        if (continent == null) {
            throw new IllegalArgumentException("id에 해당하는 continent가 없음");
        }

        Alert alert = alertRepository.findById(request.alertId()).orElse(null);
        if (alert == null) {
            throw new IllegalArgumentException("id에 해당하는 alert가 없음");
        }

        Country country = countryRepository.save(Country.builder()
                .continent(continent)
                .alert(alert)
                .countryEngName(request.countryEngName())
                .countryIsoAlp2(request.countryIsoAlp2())
                .countryName(request.countryName())
                .build());

        return CountryResponse.of(country);
    }
}
