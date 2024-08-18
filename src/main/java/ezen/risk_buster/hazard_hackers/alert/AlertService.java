package ezen.risk_buster.hazard_hackers.alert;

import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final CountryRepository countryRepository;

    public AlertService(AlertRepository alertRepository, CountryRepository countryRepository) {
        this.alertRepository = alertRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public AlertResponseDto create(AlertRequestDto request) {
        Country country = countryRepository.findByCountryEngNameAndIsDeletedFalse(request.countryEngName());

        Alert alert = Alert.builder()
                .country(country)
                .level(request.level())
                .message(request.message())
                .description(request.description())
                .regionType(request.regionType())
                .remark(request.remark())
                .dangMapDownloadUrl(request.dang_map_download_url())
                .writtenDate(request.written_dt())
                .build();
        Alert savedAlert = alertRepository.save(alert);

        return AlertResponseDto.builder()
                .countryEngName(savedAlert.getCountry().getCountryEngName())
                .id(savedAlert.getId())
                .level(savedAlert.getLevel())
                .message(savedAlert.getMessage())
                .description(savedAlert.getDescription())
                .regionType(savedAlert.getRegionType())
                .remark(savedAlert.getRemark())
                .dang_map_download_url(savedAlert.getDangMapDownloadUrl())
                .written_dt(savedAlert.getWrittenDate())
                .build();
    }

    public List<AlertResponseDto> findAll(String countryEngName) {

        List<Alert> alerts = new ArrayList<>();
        if (countryEngName == null) {
             alerts = alertRepository.findAll();
        }

        if (countryEngName != null) {
            alerts = alertRepository.findAllByCountryEngName(countryEngName);
        }


        return alerts.stream()
            .map(a -> AlertResponseDto.builder()
                    .id(a.getId())
                    .countryEngName(a.getCountry().getCountryEngName())
                    .level(a.getLevel())
                    .message(a.getMessage())
                    .description(a.getDescription())
                    .regionType(a.getRegionType())
                    .remark(a.getRemark())
                    .dang_map_download_url(a.getDangMapDownloadUrl())
                    .written_dt(a.getWrittenDate())
                    .build())
            .sorted(Comparator.comparing(AlertResponseDto::written_dt).reversed())
            .toList();
    }

    @Transactional
    public AlertResponseDto findById(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElse(null);

        if (alert == null) {
            throw new EntityNotFoundException("여행 경보를 찾을 수 없습니다.");
        }

        return AlertResponseDto.builder()
                .countryEngName(alert.getCountry().getCountryEngName())
                .id(alert.getId())
                .level(alert.getLevel())
                .message(alert.getMessage())
                .description(alert.getDescription())
                .regionType(alert.getRegionType())
                .remark(alert.getRemark())
                .dang_map_download_url(alert.getDangMapDownloadUrl())
                .written_dt(alert.getWrittenDate())
                .build();
    }

    @Transactional
    public AlertResponseDto update(Long id, AlertRequestDto request) {

        Alert alert = alertRepository.findById(id)
                .orElse(null);

        if (alert == null) {
            throw new EntityNotFoundException("여행 경보를 찾을 수 없습니다.");
        }

        alert.updateMessage(request.message());

        Alert savedAlert = alertRepository.save(alert);

        return AlertResponseDto.builder()
                .countryEngName(savedAlert.getCountry().getCountryEngName())
                .id(savedAlert.getId())
                .level(savedAlert.getLevel())
                .message(savedAlert.getMessage())
                .description(savedAlert.getDescription())
                .regionType(savedAlert.getRegionType())
                .remark(savedAlert.getRemark())
                .dang_map_download_url(savedAlert.getDangMapDownloadUrl())
                .written_dt(alert.getWrittenDate())
                .build();
    }

    @Transactional
    public void delete(Long id) {

        Alert alert = alertRepository.findByIdAndIsDeletedFalse(id);

        if (alert == null) {
            throw new EntityNotFoundException("여행 경보를 찾을 수 없습니다.");
        }

        alert.softDelete();
        alertRepository.delete(alert);
    }
}
