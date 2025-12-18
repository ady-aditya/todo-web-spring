package com.adi.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class AppSecurityConfig {

    private final JwtFilter jwtFilter;

    // Constructor injection of JwtFilter
    public AppSecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Link to the CORS config
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/user/login").permitAll()
                        .anyExchange().authenticated())
                // Add JWT filter before the authentication filter
                .addFilterAt(jwtFilter,
                        org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(withDefaults())
                // .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public org.springframework.web.cors.reactive.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        // Allow specific origins (e.g., your frontend). Use "*" for development if
        // needed, but specific is safer.
        configuration.setAllowedOriginPatterns(java.util.List.of("http://localhost:5*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(java.util.List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
