package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.model.LoginAttempt;
import setecolinas.com.sis_task_manager.repository.LoginAttemptRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Transactional
    public void addLoginAttempt(String email, boolean success) {
        log.info("Adicionando tentativa de login para o email: {}. Sucesso: {}", email, success);
        LoginAttempt loginAttempt = new LoginAttempt(email, success, LocalDateTime.now());
        this.loginAttemptRepository.save(loginAttempt);
        log.info("Tentativa de login salva para o email: {}", email);
    }

    public List<LoginAttempt> findRecentLoginAttempts(String email) {
        log.info("Buscando tentativas de login recentes para o email: {}", email);
        List<LoginAttempt> recentAttempts = loginAttemptRepository.findRecent(email);
        log.info("Foram encontradas {} tentativas de login recentes para o email: {}", recentAttempts.size(), email);
        return recentAttempts;
    }

    @Transactional
    public void loginSucceeded(String key) {
        log.info("Login bem-sucedido para o usuário: {}", key);
        attemptsCache.remove(key);
        log.info("Tentativas de login falhas removidas da cache para o usuário: {}", key);
    }

    @Transactional
    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
        log.warn("Tentativa de login falha para o usuário: {}. Número de tentativas: {}", key, attempts);
    }

    public boolean isBlocked(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        boolean blocked = attempts >= MAX_ATTEMPTS;
        if (blocked) {
            log.warn("Usuário bloqueado devido a muitas tentativas falhadas: {}", key);
        } else {
            log.info("Usuário não bloqueado: {}", key);
        }
        return blocked;
    }

    @Transactional
    public void resetAttemptsAfterTimeout(String key, long timeout, TimeUnit unit) {
        log.info("Redefinindo tentativas de login para o usuário: {} após timeout de {} {}", key, timeout, unit.name());
        attemptsCache.computeIfPresent(key, (k, v) -> {
            try {
                unit.sleep(timeout);
                log.info("Timeout concluído. Redefinindo tentativas de login para o usuário: {}", key);
                return 0;
            } catch (InterruptedException e) {
                log.error("Erro ao tentar redefinir as tentativas de login para o usuário: {}", key, e);
                Thread.currentThread().interrupt();
                return v;
            }
        });
    }
}


