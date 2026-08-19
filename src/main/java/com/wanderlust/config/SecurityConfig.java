package com.wanderlust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public authentication endpoints
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login"
                ).permitAll()

                // Temporary: property APIs
                .requestMatchers("/api/properties/**")
                .permitAll()

                .anyRequest()
                .authenticated()
            );

        return http.build();
    }
}