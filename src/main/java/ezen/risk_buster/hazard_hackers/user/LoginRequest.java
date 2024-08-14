package ezen.risk_buster.hazard_hackers.user;

public record LoginRequest(
        String userEmail,
        String password
) {
}
