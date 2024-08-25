package setecolinas.com.sis_task_manager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.model.LoginAttempt;
import setecolinas.com.sis_task_manager.repository.LoginAttemptRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Transactional
    public void addLoginAttempt(String email, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt(email, success, LocalDateTime.now());
        this.loginAttemptRepository.save(loginAttempt);
    }

    public List<LoginAttempt> findRecentLoginAttempts(String email) {
        return loginAttemptRepository.findRecent(email);
    }
}
