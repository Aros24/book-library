package com.bookrental.security;

import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.config.exceptions.UnauthorizedException;
import com.bookrental.security.filters.JwtAuthenticationFilter;
import com.bookrental.security.jwt.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                        .requestMatchers("/auth/v1/login", "/auth/v1/register", "/public/**").permitAll()
                        .requestMatchers("/v1/**").hasAnyRole(ROLE_BASIC, ROLE_ADMIN)
                        .requestMatchers("/v1/users/accounts**").hasAnyRole(ROLE_ADMIN)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // remove default Spring "ROLE_" prefix
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    public static void checkUserAccess(String providedPublicId) throws ForbiddenException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String loggedInUserPublicId = token.getPublicId();
        String loggedInUserRole = token.getRole();
        if (!loggedInUserPublicId.equals(providedPublicId) && !ROLE_ADMIN.equals(loggedInUserRole)) {
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
    }

}