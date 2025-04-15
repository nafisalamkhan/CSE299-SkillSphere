package com.cse299.skillSphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // Disable CSRF for chat endpoints
                        .ignoringRequestMatchers("/user/courses/chat", "/user/courses/stream-chat")
                )
                .authorizeHttpRequests(auth -> auth
                        // Allow access to chat endpoints
                        .requestMatchers("/user/courses/**").permitAll()
                        // Add other security rules as needed
                        .anyRequest().authenticated()
                );

        return http.build();
    } */
}