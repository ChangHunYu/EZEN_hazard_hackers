package ezen.risk_buster.hazard_hackers.alert;

import java.time.LocalDateTime;

public record AlertRequestDto(
        Long level,
        String message,
        String description,
        String regionType,
        String remark,
        String dang_map_download_url,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
