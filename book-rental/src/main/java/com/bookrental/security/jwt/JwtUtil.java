package com.bookrental.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String secret = "your_jwt_secret_key"; // Secret key for signing the JWT
    private final long expiration = 15 * 60 * 1000; // Access token expiration (15 minutes)

    // Generate a new JWT token
    public String getAccessToken(String publicId) {
        return createToken(publicId);
    }

    private SecretKey createSigningKey() {
        byte[] keyBytes = secret.getBytes();  // Use a secure encoding (e.g., Base64) for production secrets
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Create the JWT token
    private String createToken(String subject) {
        return Jwts.builder()
                .subject(subject) // Use email as the subject
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Set expiration
                .signWith(createSigningKey())  // Sign with the secret key
                .compact();
    }

    // Extract email from JWT token
    public String extractPublicId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validate the JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(createSigningKey()).build().parseSignedClaims(token);
            return true; // Valid token
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    // Extract a claim (e.g., email or publicId) from the JWT token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().verifyWith(createSigningKey()).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

}
