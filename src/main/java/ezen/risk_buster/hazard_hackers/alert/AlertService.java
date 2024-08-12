package ezen.risk_buster.hazard_hackers.alert;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
