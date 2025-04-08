package setecolinas.com.sis_task_manager.subscription;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndpointSecurity {
    /**
     * Define a regra de segurança aplicada ao endpoint.
     * Exemplos: "permitAll", "authenticated", "hasRole('ROLE_USER')"
     */
    String rule() default "authenticated";  // Valor padrão é "authenticated"

    /**
     * Valor opcional para roles ou authorities específicos.
     * Pode ser "ROLE_USER", "ROLE_ADMIN", etc.
     * Segue abaixo alguns exemplos de como utilizar a assinatura no endpoint
     * @EndpointSecurity(rule = "permitAll")
     * @EndpointSecurity(rule = "hasRole", roles = {"USER", "ADMIN"})
     * @EndpointSecurity(rule = "denyAll")
     * @EndpointSecurity(rule = "hasAuthority", roles = {"ROLE_ADMIN"})
     */
    String[] roles() default {};
}

