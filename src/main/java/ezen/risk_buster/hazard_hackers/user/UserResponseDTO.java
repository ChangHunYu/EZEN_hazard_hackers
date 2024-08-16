package ezen.risk_buster.hazard_hackers.user;

import lombok.Builder;

@Builder
public record UserResponseDTO(
        Long id,
        String name,
        String email
) {
}
