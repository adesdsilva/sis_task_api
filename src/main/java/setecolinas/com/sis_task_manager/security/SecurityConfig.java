package setecolinas.com.sis_task_manager.security;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import setecolinas.com.sis_task_manager.subscription.EndpointSecurity;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, JwtAuthFilter jwtAuthFilter, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
//        return http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> {
//                    Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//                    handlerMethods.forEach((key, handlerMethod) -> {
//                        EndpointSecurity endpointSecurity = handlerMethod.getMethodAnnotation(EndpointSecurity.class);
//                        String[] patterns = key.getPatternsCondition() != null ? key.getPatternsCondition().getPatterns().toArray(new String[0]) : new String[0];
//
//                        if (endpointSecurity != null) {
//                            log.info("Processing endpoint: {} - Rule: {}", Arrays.toString(patterns), endpointSecurity.rule());
//                            switch (endpointSecurity.rule()) {
//                                case "permitAll":
//                                    auth.requestMatchers(patterns).permitAll();
//                                    break;
//                                default:
//                                    auth.requestMatchers(patterns).authenticated();
//                            }
//                        } else {
//                            log.warn("No security annotation found for endpoint: {}", Arrays.toString(patterns));
//                        }
//                    });
//                    auth.anyRequest().authenticated();
//                })
//                .authenticationManager(authenticationManager)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager)
            throws Exception {
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
                        .requestMatchers(HttpMethod.POST, "/authenticate/unblock").permitAll()
                        // GraphQL endpoints
                        .requestMatchers(HttpMethod.POST, "/graphql").permitAll()
                        .requestMatchers(HttpMethod.GET, "/graphiql").permitAll() // If using GraphiQL
                        .requestMatchers(HttpMethod.GET, "/playground").permitAll() // If using GraphQL Playground

                        .anyRequest().authenticated())
                .authenticationManager(authenticationManager)
                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

}
