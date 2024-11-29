package com.bookrental.security;

import com.bookrental.security.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_BASIC = "basic";

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // for all
                        .requestMatchers("/auth/v1/login", "/auth/v1/register", "/public/**").permitAll()
                        // for admin only
                        .requestMatchers("/v1/users/accounts**", "/v1/authors**", "/v1/books/add", "/v1/books/{publicId}/amount").hasRole(ROLE_ADMIN)
                        // shared
                        .requestMatchers("/v1/books/{publicId}", "/v1/books**",
                                "/v1/users/{publicId}", "/v1/users/{publicId}/status").hasAnyRole(ROLE_BASIC, ROLE_ADMIN)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // remove default Spring "ROLE_" prefix
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

}