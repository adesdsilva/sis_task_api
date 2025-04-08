package setecolinas.com.sis_task_manager.subscription;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            EndpointSecurity securityAnnotation = method.getMethodAnnotation(EndpointSecurity.class);

            if (securityAnnotation != null) {
                String rule = securityAnnotation.rule();
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                switch (rule) {
                    case "permitAll":
                        return true; // Acesso permitido para todos

                    case "denyAll":
                        throw new AccessDeniedException("Access denied");

                    case "authenticated":
                        return authentication != null && authentication.isAuthenticated();

                    case "fullyAuthenticated":
                        return authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().isEmpty();

                    case "anonymous":
                        return authentication == null || !authentication.isAuthenticated();

                    case "rememberMe":
                        // Lógica para verificar remember-me (customize conforme a necessidade)
                        return authentication != null && authentication.getAuthorities().stream()
                                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_REMEMBER_ME"));

                    case "hasRole":
                        return checkRole(authentication, securityAnnotation.roles());

                    case "hasAuthority":
                        return checkAuthority(authentication, securityAnnotation.roles());

                    default:
                        throw new AccessDeniedException("Invalid security rule");
                }
            }
        }
        return true;
    }

    private boolean checkRole(Authentication authentication, String[] roles) {
        if (authentication == null || roles.length == 0) {
            return false;
        }
        return Arrays.stream(roles)
                .anyMatch(role -> authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role)));
    }

    private boolean checkAuthority(Authentication authentication, String[] authorities) {
        if (authentication == null || authorities.length == 0) {
            return false;
        }
        return Arrays.stream(authorities)
                .anyMatch(authority -> authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)));
    }
}

