package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import jakarta.transaction.Transactional;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        Country country = countryRepository.save(Country.builder()
                .continent(continent)
                .countryEngName(request.countryEngName())
                .countryIsoAlp2(request.countryIsoAlp2())
                .countryName(request.countryName())
                .flagDownloadUrl(request.flagDownloadUrl())
                .mapDownloadUrl(request.mapDownloadUrl())
                .build());;

        return CountryResponse.of(country);
    }

    public CountryResponse findById(Long id) {
        Country country = countryRepository.findByIdAndIsDeletedFalse(id);
        return CountryResponse.of(country);
    }

    public List<CountryListResponse> findAll() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream().map(CountryListResponse::of)
                .sorted((c1, c2) -> c1.countryName().compareTo(c2.countryName()))
                .toList();
    }

    @Transactional
    public CountryResponse update(CountryRequest request, Long id) {
        Country country = countryRepository.findByIdAndIsDeletedFalse(id);

        if (country == null) {
            throw new IllegalArgumentException("id에 해당하는 country가 없음");
        }

        Continent continent = continentRepository.findById(request.continentId()).orElse(null);
        if (continent == null) {
            throw new IllegalArgumentException("id에 해당하는 continent가 없음");
        }

//        Alert alert = alertRepository.findById(request.alertId()).orElse(null);
//        if (alert == null) {
//            throw new IllegalArgumentException("id에 해당하는 alert가 없음");
//        }

        Country updateCountry = Country.builder()
                .id(country.getId())
                .continent(continent)
//                .alert(alert)
                .countryEngName(request.countryEngName())
                .flagDownloadUrl(request.flagDownloadUrl())
                .mapDownloadUrl(request.mapDownloadUrl())
                .countryIsoAlp2(request.countryIsoAlp2())
                .countryName(request.countryName())
                .build();

        Country savedCountry = countryRepository.save(updateCountry);

        return CountryResponse.of(savedCountry);
    }

    public void delete(Long id) {
        Country country = countryRepository.findByIdAndIsDeletedFalse(id);

        if (country == null) {
            throw new IllegalArgumentException("id에 해당하는 country가 없음");
        }

        country.softDelete();
        countryRepository.save(country);
    }
}
