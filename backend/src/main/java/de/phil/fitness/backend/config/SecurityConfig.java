package de.phil.fitness.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import javax.crypto.spec.SecretKeySpec;

import de.phil.fitness.backend.auth.AccessTokenTypeValidator;
import de.phil.fitness.backend.auth.JwtProperties;
import de.phil.fitness.backend.auth.RestAccessDeniedHandler;
import de.phil.fitness.backend.auth.RestAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    public SecurityConfig() { }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the security filter chain for the application, configuring CORS, CSRF, session management, and request authorization.
     * @param http HttpSecurity object used to configure the security settings for HTTP requests.
     * @param authenticationEntryPoint Writes the shared error body when a request carries no valid token.
     * @param accessDeniedHandler Writes the shared error body when the filter chain refuses an authenticated request.
     * @return SecurityFilterChain that defines the security configuration for the application.
     * @throws Exception if there is an issue configuring the security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RestAuthenticationEntryPoint authenticationEntryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/backend/auth/**").permitAll() //Note: ALL auth endpoints currently accessible/reachable
                        .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll() //Container healthcheck; exposes liveness state only, no details.
                        .requestMatchers("/error").permitAll() //Error dispatch is a forwarded request; gating it turns every 4xx into an empty 401.
                        .anyRequest().authenticated())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    /**
     * Configures the JwtDecoder bean for decoding JWT tokens using a secret key specified in the JwtProperties.
     * @param jwtProperties JwtProperties object that contains the secret key used for decoding JWT tokens.
     * @return  JwtDecoder that is configured to decode JWT tokens using the specified secret key and HMAC SHA-512 algorithm,
     *          rejecting anything that is not an access token.
     */
    @Bean
    public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        SecretKeySpec key = new SecretKeySpec(
                jwtProperties.secret().getBytes(),
                "HmacSHA512"); //Important: Algorithm needs to match to (automatically) chosen algorithm based on key.

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                .build();

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new AccessTokenTypeValidator()));

        return decoder;
    }

    /**
     * Configures CORS settings for the application, allowing requests from specified origins and defining allowed methods and headers.
     * Current architectural setup makes CORS configuration redundant, but it is included for potential future use when the frontend and backend are separated into different services.
     * @return CorsConfigurationSource that provides the CORS configuration for the application.
     */ 
    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(60L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
