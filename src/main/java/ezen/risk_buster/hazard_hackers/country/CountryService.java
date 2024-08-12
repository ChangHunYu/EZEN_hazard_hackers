package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import jakarta.transaction.Transactional;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
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

    public CountryResponse findById(Long id) {
        Country country = countryRepository.findById(id).orElse(null);
        return CountryResponse.of(country);
    }

    public List<CountryResponse> findAll() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream().map(CountryResponse::of).toList();
    }

    @Transactional
    public CountryResponse update(CountryRequest request, Long id) {
        Country country = countryRepository.findById(id).orElse(null);

        if (country == null) {
            throw new IllegalArgumentException("id에 해당하는 country가 없음");
        }

        Continent continent = continentRepository.findById(request.continentId()).orElse(null);
        if (continent == null) {
            throw new IllegalArgumentException("id에 해당하는 continent가 없음");
        }

        Alert alert = alertRepository.findById(request.alertId()).orElse(null);
        if (alert == null) {
            throw new IllegalArgumentException("id에 해당하는 alert가 없음");
        }

        Country updateCountry = Country.builder()
                .id(country.getId())
                .continent(continent)
                .alert(alert)
                .countryEngName(request.countryEngName())
                .flagDownloadUrl(request.flagDownloadUrl())
                .mapDownloadUrl(request.mapDownloadUrl())
                .countryIsoAlp2(request.countryIsoAlp2())
                .countryName(request.countryName())
                .build();

        Country savedCountry = countryRepository.save(updateCountry);

        return CountryResponse.of(savedCountry);
    }
}
