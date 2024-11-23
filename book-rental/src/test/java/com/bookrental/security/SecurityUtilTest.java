package com.bookrental.security;

import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.jwt.JwtAuthenticationToken;
import com.bookrental.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @InjectMocks
    private SecurityUtil securityUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String PUBLIC_ID = "12345";
    private static final String DIFFERENT_PUBLIC_ID = "6789";
    private static final String BASIC_ROLE = "basic";
    private static final String ADMIN_ROLE = "admin";
    private static final String PASSWORD = "password";
    private static final String DIFFERENT_PASSWORD = "different password";

    @Test
    void checkUserAccess_ShouldNotThrowException_WhenUserHasAccess() {
        // Given
        when(jwtUtil.getTokenFromSession(PUBLIC_ID)).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getPublicId()).thenReturn(PUBLIC_ID);
        when(jwtAuthenticationToken.getRole()).thenReturn(BASIC_ROLE);

        // When & Then
        assertDoesNotThrow(() -> securityUtil.checkUserAccess(PUBLIC_ID));
    }

    @Test
    void checkUserAccess_ShouldNotThrowException_WhenUserIsAdmin() {
        // Given
        when(jwtUtil.getTokenFromSession(PUBLIC_ID)).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getPublicId()).thenReturn(DIFFERENT_PUBLIC_ID);
        when(jwtAuthenticationToken.getRole()).thenReturn(ADMIN_ROLE);

        // When & Then
        assertDoesNotThrow(() -> securityUtil.checkUserAccess(PUBLIC_ID));
    }

    @Test
    void checkUserAccess_ShouldThrowForbiddenException_WhenUserDoesNotHaveAccess() {
        // Given
        when(jwtUtil.getTokenFromSession(PUBLIC_ID)).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getPublicId()).thenReturn(DIFFERENT_PUBLIC_ID);
        when(jwtAuthenticationToken.getRole()).thenReturn(BASIC_ROLE);

        // When & Then
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> securityUtil.checkUserAccess(PUBLIC_ID)
        );
        assertEquals("You do not have permission to access this resource.", exception.getMessage());
    }

    @Test
    void checkIfAdmin_ShouldReturnTrue_WhenUserIsAdmin() {
        // Given
        when(jwtUtil.getTokenFromSession(PUBLIC_ID)).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getRole()).thenReturn(ADMIN_ROLE);

        // When & Then
        assertTrue(securityUtil.checkIfAdmin(PUBLIC_ID));
    }

    @Test
    void checkIfAdmin_ShouldReturnFalse_WhenUserIsNotAdmin() {
        // Given
        when(jwtUtil.getTokenFromSession(PUBLIC_ID)).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getRole()).thenReturn(BASIC_ROLE);

        // When & Then
        assertFalse(securityUtil.checkIfAdmin(PUBLIC_ID));
    }

    @Test
    void checkIfPasswordMatches_ShouldReturnTrue_WhenPasswordsMatch() {
        // Given
        String encodedPassword = passwordEncoder.encode(PASSWORD);

        // When & Then
        assertTrue(securityUtil.checkIfPasswordMatches(PASSWORD, encodedPassword));
    }

    @Test
    void checkIfPasswordMatches_ShouldReturnFalse_WhenPasswordsDoNotMatch() {
        // Given
        String encodedPassword = passwordEncoder.encode(DIFFERENT_PASSWORD);

        // When & Then
        assertFalse(securityUtil.checkIfPasswordMatches(PASSWORD, encodedPassword));
    }

    @Test
    void encryptPassword_ShouldReturnEncodedPassword() {
        // Given & When
        String encodedPassword = securityUtil.encryptPassword(PASSWORD);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(PASSWORD, encodedPassword);
        assertTrue(passwordEncoder.matches(PASSWORD, encodedPassword));
    }

}
