package ezen.risk_buster.hazard_hackers.alert;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AlertResponseDto(
        Long id,
        Long level,
        String message,
        String description,
        String regionType,
        String remark,
        String dang_map_download_url,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        LocalDateTime updatedAt
) {
}
