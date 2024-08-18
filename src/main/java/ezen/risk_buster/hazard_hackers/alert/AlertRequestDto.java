package ezen.risk_buster.hazard_hackers.alert;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AlertRequestDto(
        String countryEngName,
        Long level,
        String message,
        String description,
        String regionType,
        String remark,
        String dang_map_download_url,
        LocalDate written_dt
) {
}
