package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.model.PasswordResetToken;
import setecolinas.com.sis_task_manager.model.User;
import setecolinas.com.sis_task_manager.repository.PasswordResetTokenRepository;
import setecolinas.com.sis_task_manager.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository, UserRepository userRepository, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Generating password reset token for user: {}", email);

        // Gerar um novo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));

        // Remover tokens antigos para o usuário
        tokenRepository.deleteByUser(user);

        // Salvar o novo token
        tokenRepository.save(passwordResetToken);

        log.info("Password reset token generated and saved for user: {}", email);

        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        log.info("Validating password reset token: {}", token);
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password using token: {}", token);
        PasswordResetToken passwordResetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = passwordResetToken.getUser();
        user.setPassword(newPassword);
        this.userRepository.save(user);

        tokenRepository.delete(passwordResetToken);
        log.info("Password reset successful for user: {}", user.getEmail());
    }
}

