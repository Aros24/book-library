package com.bookrental.security;

import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.jwt.JwtAuthenticationToken;
import com.bookrental.security.jwt.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String ROLE_ADMIN = "admin";

    public SecurityUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void checkUserAccess(String providedPublicId) throws ForbiddenException {
        JwtAuthenticationToken token = (JwtAuthenticationToken) jwtUtil.getTokenFromSession(providedPublicId);
        String loggedInUserPublicId = token.getPublicId();
        String loggedInUserRole = token.getRole();
        if (!loggedInUserPublicId.equals(providedPublicId) && !ROLE_ADMIN.equals(loggedInUserRole)) {
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
    }

    public boolean checkIfAdmin(String providedPublicId) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) jwtUtil.getTokenFromSession(providedPublicId);
        String loggedInUserRole = token.getRole();
        return ROLE_ADMIN.equals(loggedInUserRole);
    }

    public boolean checkIfPasswordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
