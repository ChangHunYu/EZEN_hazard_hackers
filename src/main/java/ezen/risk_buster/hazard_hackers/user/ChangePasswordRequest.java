package ezen.risk_buster.hazard_hackers.user;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
