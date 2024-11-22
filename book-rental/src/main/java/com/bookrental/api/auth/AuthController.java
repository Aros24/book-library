package com.bookrental.api.auth;

import com.bookrental.api.auth.request.LoginRequest;
import com.bookrental.api.auth.request.RegisterRequest;
import com.bookrental.api.auth.response.AuthResponse;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.SecurityConstants;
import com.bookrental.service.auth.AuthService;
import com.bookrental.service.auth.LoginDto;
import com.bookrental.service.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication-related endpoints")
@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user with the provided details and returns the user's public ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, missing required fields", content = @Content(mediaType = "application/json")),
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        UserDto user = authService.registerUser(request);
        return AuthResponse.builder()
                .userPublicId(user.getPublicId())
                .build();
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login an existing user",
            description = "Authenticates a user with their credentials and returns an authorization token along with the user's public ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws ForbiddenException {
        LoginDto login = authService.loginUser(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, SecurityConstants.BEARER.getValue() + login.getJwt())
                .body(AuthResponse.builder()
                        .userPublicId(login.getPublicId())
                        .role(login.getRole())
                        .build());
    }

}