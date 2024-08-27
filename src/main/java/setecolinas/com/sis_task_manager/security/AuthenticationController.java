package setecolinas.com.sis_task_manager.security;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import setecolinas.com.sis_task_manager.service.LoginAttemptService;
import setecolinas.com.sis_task_manager.service.UserService;

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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            loginAttemptService.addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = jwtUtil.generateToken(request.email());
        loginAttemptService.addLoginAttempt(request.email(), true);
        return ResponseEntity.ok(new AuthenticationResponse(request.email(), token));
    }
}
