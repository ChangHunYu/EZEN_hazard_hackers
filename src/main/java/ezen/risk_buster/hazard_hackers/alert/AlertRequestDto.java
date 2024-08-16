package ezen.risk_buster.hazard_hackers.alert;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AlertRequestDto(
        Long level,
        @NotNull String message,
        String description,
        String regionType,
        String remark,
        @NotNull String dang_map_download_url
) {
}
