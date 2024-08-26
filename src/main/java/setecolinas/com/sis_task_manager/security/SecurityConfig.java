package setecolinas.com.sis_task_manager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        Set permissions on endpoints
                .authorizeHttpRequests(auth -> auth
//            our public endpoints
                        .requestMatchers(HttpMethod.POST, "/authenticate/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tasks/{listId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/list/{listId}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/tasks/{taskId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/tasks/{taskId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/tasks/{taskId}/complete").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/completed").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/pending").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tasks/favorite").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/tasks/lists/{taskListId}/completed").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/tasks/{taskId}/status").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/tasks/{taskId}/favorite").permitAll()
                        .requestMatchers(HttpMethod.GET, "/task-lists/ordered").permitAll()
                        .requestMatchers(HttpMethod.GET, "/task-lists").permitAll()
                        .requestMatchers(HttpMethod.GET, "/task-lists/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/task-lists").permitAll()
                        .requestMatchers(HttpMethod.GET, "/subtasks/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/subtasks").permitAll()
                        .requestMatchers(HttpMethod.GET, "/subtasks/{id}").permitAll()
//            our private endpoints
                        .anyRequest().authenticated())
                .authenticationManager(authenticationManager)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

}
