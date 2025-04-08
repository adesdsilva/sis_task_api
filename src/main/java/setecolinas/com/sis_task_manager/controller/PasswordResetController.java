package setecolinas.com.sis_task_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.businessRole.EmailService;
import setecolinas.com.sis_task_manager.service.PasswordResetService;

@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public PasswordResetController(PasswordResetService passwordResetService, EmailService emailService) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        String token = passwordResetService.createPasswordResetToken(email);

        String resetLink = "http://localhost:8080/password-reset/confirm?token=" + token;

        emailService.sendEmail(
                email,
                "Password Reset Request",
                "Click the link to reset your password: " + resetLink
        );

        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam("token") String token,
                                                       @RequestParam("newPassword") String newPassword) {
        if (!passwordResetService.validatePasswordResetToken(token)) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}

