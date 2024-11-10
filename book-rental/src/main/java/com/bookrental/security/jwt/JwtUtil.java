package com.bookrental.security.jwt;

import com.bookrental.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.refresh-time}")
    private long refreshTime;
    private final String PUBLIC_ID_MAP_KEY = "publicId";
    private final int BEARER_LENGTH = 7;

    public String generateToken(String email, String publicId) {
        Instant utcTimeNow = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim(PUBLIC_ID_MAP_KEY, publicId)
                .issuedAt(Date.from(utcTimeNow))
                .expiration(Date.from(utcTimeNow.plusMillis(expiration)))
                .signWith(createSigningKey())
                .compact();
    }

    public boolean validateRawToken(String token) {
        try {
            Jws<Claims> jwsClaims =  Jwts.parser().verifyWith(createSigningKey()).build().parseSignedClaims(token);
            Claims claims = jwsClaims.getPayload();
            return !claims.getExpiration().before(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmailFromRawToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractPublicIdFromRawToken(String token) {
        return extractClaim(token, claims -> claims.get(PUBLIC_ID_MAP_KEY, String.class));
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.AUTHORIZATION.getValue());
        if (bearerToken != null && bearerToken.startsWith(SecurityConstants.BEARER.getValue())) {
            return bearerToken.substring(BEARER_LENGTH);
        }
        return null;
    }

    // Less than 2 minutes to expire
    public boolean isTokenExpiringSoon(String token) {
        Claims claims = Jwts.parser().verifyWith(createSigningKey()).build().parseSignedClaims(token).getPayload();
        long currentTime = Instant.now().toEpochMilli();
        long expirationTime = claims.getExpiration().getTime();
        return expirationTime - currentTime < refreshTime;
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().verifyWith(createSigningKey()).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey createSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
