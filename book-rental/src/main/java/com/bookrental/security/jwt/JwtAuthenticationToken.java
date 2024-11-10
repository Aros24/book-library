package com.bookrental.security.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    String email;
    String role;
    String publicId;

    public JwtAuthenticationToken(String email, String role, String publicId) {
        super(Collections.singletonList(new SimpleGrantedAuthority(role)));
        this.email = email;
        this.role = role;
        this.publicId = publicId;
        setAuthenticated(true);
    }

    // stateless auth - do not keep credentials
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

}