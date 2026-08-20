package com.wanderlust.config;

import com.wanderlust.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // JWT = stateless authentication
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authorizeHttpRequests(auth -> auth

                // Authentication endpoints
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login"
                ).permitAll()

                // Anyone can browse properties
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/properties/**"
                ).permitAll()

                // Creating a property requires login
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/properties"
                ).authenticated()

                // Updating/deleting requires login
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/properties/**"
                ).authenticated()

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/properties/**"
                ).authenticated()
                
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/bookings"
                ).authenticated()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Run JWT filter before Spring's username/password filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );
        

        return http.build();
    }
}