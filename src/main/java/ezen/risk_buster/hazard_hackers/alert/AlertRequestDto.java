package ezen.risk_buster.hazard_hackers.alert;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AlertRequestDto(
        String countryEngName,
        Long level,
        @NotNull String message,
        String description,
        String regionType,
        String remark,
        @NotNull String dang_map_download_url
        String dang_map_download_url,
        LocalDate written_dt
) {
}
