package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.model.PasswordResetToken;
import setecolinas.com.sis_task_manager.model.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
