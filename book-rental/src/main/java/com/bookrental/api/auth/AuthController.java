package com.bookrental.api.auth;

import com.bookrental.api.auth.request.LoginRequest;
import com.bookrental.api.auth.request.RegisterRequest;
import com.bookrental.api.auth.response.AuthResponse;
import com.bookrental.security.SecurityConstants;
import com.bookrental.security.jwt.JwtUtil;
import com.bookrental.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthResponse.builder()
                .userPublicId(authService.registerUser(request).getPublicId())
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String jwt = authService.loginUser(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, SecurityConstants.BEARER.getValue() + jwt)
                .body(AuthResponse.builder()
                        .userPublicId(jwtUtil.extractPublicIdFromRawToken(jwt))
                        .build());
    }

}