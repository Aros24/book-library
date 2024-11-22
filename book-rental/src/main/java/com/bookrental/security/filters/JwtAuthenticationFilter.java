package com.bookrental.security.filters;

import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.persistence.repositories.UserRepository;
import com.bookrental.security.SecurityConstants;
import com.bookrental.security.jwt.JwtAuthenticationToken;
import com.bookrental.security.jwt.JwtUtil;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/v1/")) {
            String token = jwtUtil.getJwtFromRequest(request);
            if (token != null && jwtUtil.validateRawToken(token)) {
                String email = jwtUtil.extractEmailFromRawToken(token);
                String publicId = jwtUtil.extractPublicIdFromRawToken(token);
                UserDto user = userService.getUserByPublicId(publicId);

                if (user.isDeleted()) {
                    throw new ForbiddenException("Account has been deleted");
                }
                if (jwtUtil.isTokenExpiringSoon(token)) {
                    token = jwtUtil.generateToken(email, publicId);
                }

                Authentication authentication = new JwtAuthenticationToken(email, user.getRole(), publicId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                response.setHeader(SecurityConstants.AUTHORIZATION.getValue(), SecurityConstants.BEARER.getValue() + token);
            }
        }

        filterChain.doFilter(request, response);
    }

}
