package com.bookrental.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String publicId;
    private final String role;

    public JwtAuthenticationToken(String publicId, String role) {
        super(Collections.singletonList(new SimpleGrantedAuthority(role)));  // Dynamically assign the user's role
        this.publicId = publicId;
        this.role = role;
        setAuthenticated(true);  // Set the authentication flag
    }

    @Override
    public Object getCredentials() {
        return null;  // Credentials are not stored (stateless authentication)
    }

    @Override
    public Object getPrincipal() {
        return publicId;  // Return the publicId as the principal
    }

}