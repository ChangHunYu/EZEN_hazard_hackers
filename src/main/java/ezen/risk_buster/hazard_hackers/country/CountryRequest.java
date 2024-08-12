package ezen.risk_buster.hazard_hackers.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record CountryRequest(
        @NotNull
        Long continentId,
        @NotNull
        Long alertId,
        @NotBlank(message = "영문 국가 이름은 필수입니다.")
        String countryEngName,
        @Pattern(regexp = "^[A-Z]{2}$", message = "countryIsoAlp2는 유효한 ISO 3166-1 alpha-2 코드여야 합니다.")
        String countryIsoAlp2,
        @NotBlank(message = "국가 이름은 필수입니다.")
        String countryName,
        @NotBlank(message = "국기 다운로드 URL은 필수입니다.")
        @URL(message = "flagDownloadUrl는 유효한 URL이어야 합니다.")
        String flagDownloadUrl,
        @NotBlank(message = "지도 다운로드 URL은 필수입니다.")
        @URL(message = "mapDownloadUrl는 유효한 URL이어야 합니다.")
        String mapDownloadUrl
) {
}
