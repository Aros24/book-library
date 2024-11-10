package com.bookrental.security.filters;

import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.config.exceptions.UnauthorizedException;
import com.bookrental.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class PublicIdCheckFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Set<String> FILTERED_PATHS = Set.of("/v1/users/");

    @Autowired
    public PublicIdCheckFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (shouldFilter(request.getRequestURI())) {
            String token = jwtUtil.getJwtFromRequest(request);
            String pathPublicId = extractPublicIdFromPath(request);

            if (token != null && jwtUtil.validateRawToken(token) && pathPublicId != null) {
                String tokenPublicId = jwtUtil.extractPublicIdFromRawToken(token);
                if (!tokenPublicId.equals(pathPublicId)) {
                    throw new ForbiddenException(String.format("User publicId mismatch for id: %s", pathPublicId));
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldFilter(String requestUri) {
        return FILTERED_PATHS.stream()
                .anyMatch(requestUri::startsWith);
    }

    // extracts the last path element
    private String extractPublicIdFromPath(HttpServletRequest request) {
        String[] pathSegments = request.getRequestURI().split("/");
        return pathSegments[pathSegments.length - 1];
    }

}
