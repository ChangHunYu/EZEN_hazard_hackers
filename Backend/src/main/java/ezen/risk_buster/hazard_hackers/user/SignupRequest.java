package ezen.risk_buster.hazard_hackers.user;

public record SignupRequest(
        String email,
        String username,
        String password
) {
}
