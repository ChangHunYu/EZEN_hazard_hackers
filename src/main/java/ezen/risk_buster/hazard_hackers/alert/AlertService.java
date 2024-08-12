package ezen.risk_buster.hazard_hackers.alert;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public AlertResponseDto create(AlertRequestDto request) {

        Alert alert = Alert.builder()
                .level(request.level())
                .message(request.message())
                .description(request.description())
                .regionType(request.regionType())
                .remark(request.remark())
                .dangMapDownloadUrl(request.dang_map_download_url())
                .build();
        Alert savedAlert = alertRepository.save(alert);

        return AlertResponseDto.builder()
                .id(savedAlert.getId())
                .level(savedAlert.getLevel())
                .message(savedAlert.getMessage())
                .description(savedAlert.getDescription())
                .regionType(savedAlert.getRegionType())
                .remark(savedAlert.getRemark())
                .dang_map_download_url(savedAlert.getDangMapDownloadUrl())
                .createdAt(savedAlert.getCreatedAt())
                .deletedAt(savedAlert.getDeletedAt())
                .updatedAt(savedAlert.getUpdatedAt())
                .build();
    }

    public List<AlertResponseDto> findAll() {
        List<Alert> alerts = alertRepository.findAll();

        return alerts.stream()
                .map(a -> AlertResponseDto.builder()
                        .id(a.getId())
                        .level(a.getLevel())
                        .message(a.getMessage())
                        .description(a.getDescription())
                        .regionType(a.getRegionType())
                        .remark(a.getRemark())
                        .dang_map_download_url(a.getDangMapDownloadUrl())
                        .createdAt(a.getCreatedAt())
                        .deletedAt(a.getDeletedAt())
                        .updatedAt(a.getUpdatedAt())
                        .build())
                .toList();
    }
}
