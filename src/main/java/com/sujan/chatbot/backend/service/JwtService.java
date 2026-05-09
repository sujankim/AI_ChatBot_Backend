package com.sujan.chatbot.backend.service;

import com.sujan.chatbot.backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;       // In milliseconds (e.g., 900000 = 15 min)

    @Value("${app.jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;      // In milliseconds (e.g., 604800000 = 7 days)

    // ─── Token Generation ─────────────────────────────────────────────────────

    /**
     * Generate an access token for a user.
     * Short-lived. Sent in Authorization header.
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        claims.put("tokenType", "access");

        return buildToken(claims, user.getEmail(), accessTokenExpiry);
    }

    /**
     * Generate a refresh token for a user.
     * Long-lived. Sent as HttpOnly cookie.
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("tokenType", "refresh");

        return buildToken(claims, user.getEmail(), refreshTokenExpiry);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiry) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)           // User's email as the "subject"
                .issuedAt(new Date())       // When was this token created
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey())  // Sign with our secret key
                .compact();                 // Build the final JWT string
    }

    // ─── Token Validation ─────────────────────────────────────────────────────

    /**
     * Validate a token. Returns true if valid, false otherwise.
     * Never throws — always returns boolean.
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);        // If this succeeds, token is valid
            return true;
        } catch (ExpiredJwtException ex) {
            System.err.println("JWT expired: " + ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            System.err.println("JWT malformed: " + ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            System.err.println("JWT signature invalid: " + ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            System.err.println("JWT unsupported: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.err.println("JWT validation error: " + ex.getMessage());
            return false;
        }
    }

    // ─── Token Extraction ─────────────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // Use our secret key to verify
                .build()
                .parseSignedClaims(token)
                .getPayload();                 // Returns the claims (payload)
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);   // Creates HMAC-SHA256 key
    }
}
