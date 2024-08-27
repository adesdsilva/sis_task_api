package setecolinas.com.sis_task_manager.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.security.AuthenticationRequest;
import setecolinas.com.sis_task_manager.security.AuthenticationResponse;
import setecolinas.com.sis_task_manager.security.JwtUtil;
import setecolinas.com.sis_task_manager.security.MyUserDetailsService;
import setecolinas.com.sis_task_manager.service.LoginAttemptService;
import setecolinas.com.sis_task_manager.service.UserService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtUtil jwtUtil,
                                    MyUserDetailsService userDetailsService,
                                    UserService userService,
                                    LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        String email = request.email();

        if (loginAttemptService.isBlocked(email)) {
            throw new LockedException("Usuário bloqueado devido a muitas tentativas de login falhadas.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
            loginAttemptService.loginSucceeded(email);
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(email);
            throw e;
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(new AuthenticationResponse(email, token));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unblock")
    public ResponseEntity<String> unblockUser(@RequestParam String email) {
        loginAttemptService.resetAttemptsAfterTimeout(email, 0, TimeUnit.SECONDS);
        return ResponseEntity.ok("Usuário " + email + " foi desbloqueado com sucesso.");
    }
}

