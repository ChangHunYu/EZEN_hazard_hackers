package ezen.risk_buster.hazard_hackers.user;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        String userEmail,
        @NotNull String password
) {
}
