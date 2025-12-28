package com.fastconnect.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@AllArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        final String[] PUBLIC_ENDPOINTS = {
                "/api/auth/register",
                "/api/auth/login",

                // Keep Swagger/API Docs public for easy testing
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml"
        };

        // Disable CSRF and allow all requests without authentication
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        .requestMatchers("/api/users/faculty-page").hasAnyRole("ADMIN", "FACULTY")
                        .requestMatchers("/api/societies/create-society/**").hasAnyRole("ADMIN", "SOCIETY_ADMIN")
                        .requestMatchers("api/societies/update/**").hasAnyRole("ADMIN", "SOCIETY_ADMIN")
                        .requestMatchers("/api/societies/change-membership/**").hasAnyRole("ADMIN", "SOCIETY_ADMIN")
                        .requestMatchers(
                                "/api/users/all",
                                "/api/admin/**",
                                "/api/users/search-by-account-status",
                                "/api/users/search-by-role-type",
                                "/api/users/check-id/**",
                                "/api/users/{userId}",
                                "/api/connections/requests/between/**",
                                "/api/posts/all"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(userDetailsService))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
//    @Bean
//    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
//        return username -> {
//            // This will be handled by the JWT filter
//            return null;
//        };
//    }
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Inject your UserDetailsService implementation
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

