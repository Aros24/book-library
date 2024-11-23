package com.bookrental.service.auth;

import com.bookrental.api.auth.request.LoginRequest;
import com.bookrental.api.auth.request.RegisterRequest;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.SecurityUtil;
import com.bookrental.security.jwt.JwtUtil;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private AuthService authService;

    private static final String PUBLIC_ID = "12345";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String INVALID_EMAIL = "invalid_email@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final String WRONG_PASSWORD = "wrong_password";
    private static final String ROLE_BASIC = "basic";

    @Test
    void registerUser_ShouldReturnNewUser_WhenRequestIsValid() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        UserDto expectedUserDto = createUserDto();

        when(securityUtil.encryptPassword(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.createUser(any(UserDto.class))).thenReturn(expectedUserDto);

        // When
        UserDto result = authService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(ROLE_BASIC, result.getRole());
        verify(securityUtil, times(1)).encryptPassword(PASSWORD);
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() throws ForbiddenException {
        // Given
        LoginRequest loginRequest = createLoginRequest(EMAIL, PASSWORD);
        UserDto userDto = createUserDto();

        String expectedToken = "test-jwt-token";

        when(userService.getUserByEmail(EMAIL)).thenReturn(userDto);
        when(securityUtil.checkIfPasswordMatches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtUtil.generateToken(userDto.getEmail(), userDto.getPublicId())).thenReturn(expectedToken);

        // When
        String result = authService.loginUser(loginRequest).getJwt();

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(userService, times(1)).getUserByEmail(EMAIL);
        verify(securityUtil, times(1)).checkIfPasswordMatches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtUtil, times(1)).generateToken(userDto.getEmail(), userDto.getPublicId());
    }

    @Test
    void loginUser_ShouldThrowBadRequestException_WhenPasswordIsInvalid() {
        // Given
        LoginRequest loginRequest = createLoginRequest(EMAIL, WRONG_PASSWORD);
        UserDto userDto = createUserDto();

        when(userService.getUserByEmail(EMAIL)).thenReturn(userDto);
        when(securityUtil.checkIfPasswordMatches(eq(WRONG_PASSWORD), eq(ENCODED_PASSWORD))).thenReturn(false);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                authService.loginUser(loginRequest)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userService, times(1)).getUserByEmail(EMAIL);
        verify(securityUtil, times(1)).checkIfPasswordMatches(WRONG_PASSWORD, ENCODED_PASSWORD);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_ShouldThrowForbiddenException_WhenAccountIsDeleted() {
        // Given
        LoginRequest loginRequest = createLoginRequest(EMAIL, PASSWORD);
        UserDto userDto = createUserDto();
        userDto.setDeleted(true);

        when(userService.getUserByEmail(EMAIL)).thenReturn(userDto);
        when(securityUtil.checkIfPasswordMatches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
                authService.loginUser(loginRequest)
        );
        assertEquals("Account has been deleted", exception.getMessage());
        verify(userService, times(1)).getUserByEmail(EMAIL);
        verify(securityUtil, times(1)).checkIfPasswordMatches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_ShouldThrowBadRequestException_WhenUserNotFound() {
        // Given
        LoginRequest loginRequest = createLoginRequest(INVALID_EMAIL, PASSWORD);

        when(userService.getUserByEmail(INVALID_EMAIL)).thenThrow(new BadRequestException("User not found"));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                authService.loginUser(loginRequest)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserByEmail(INVALID_EMAIL);
        verify(securityUtil, never()).checkIfPasswordMatches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }


    private UserDto createUserDto() {
        return UserDto.builder()
                .publicId(PUBLIC_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .role(ROLE_BASIC)
                .build();
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

}
