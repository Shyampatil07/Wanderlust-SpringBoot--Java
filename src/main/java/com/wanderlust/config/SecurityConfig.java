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

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            // 1. Disable CSRF for REST API
            .csrf(csrf -> csrf.disable())

            // 2. JWT authentication is stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // 3. Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Public authentication APIs
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login"
                ).permitAll()
                
             // Public property reviews
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/properties/*/reviews"
                ).permitAll()

                // Creating reviews requires login
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/properties/*/reviews"
                ).authenticated()
                
                //update review requires login
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/properties/*/reviews/*"
                ).authenticated()

                // Anyone can view properties
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/properties/**"
                ).permitAll()

                // Property operations require login
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/properties"
                ).authenticated()

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/properties/**"
                ).authenticated()

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/properties/**"
                ).authenticated()

             // Booking requires login
                .requestMatchers(
                        "/api/bookings/**"
                ).authenticated()
                

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // 4. Run our JWT filter before Spring's
            // username/password authentication filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}