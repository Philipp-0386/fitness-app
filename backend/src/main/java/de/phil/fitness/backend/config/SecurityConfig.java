package de.phil.fitness.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * General configuration of security features.
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates a {@link PasswordEncoder} bean using the BCrypt hashing algorithm.
     * @return returns a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures a permissive {@link SecurityFilterChain} with all security features disabled.
     *
     * <p>Temporary configuration for development — CSRF protection, form login,
     * and HTTP basic auth are all disabled. Will be replaced or edited during later stage.
     *
     * @param http  the {@link HttpSecurity} to configure
     * @return  returns configured {@link SecurityFilterChain}
     * @throws Exception throws exception if security configuration fails
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
