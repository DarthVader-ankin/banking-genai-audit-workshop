package com.fastpay.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    // Vulnerability: Hardcoded secret key
    private String jwtSecret = "mySecretKey";
    
    // Vulnerability: Excessively long token expiration
    private int jwtExpirationInMs = 604800000; // 7 days
    
    public String generateToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                // Vulnerability: Weak signature algorithm
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    public boolean validateToken(String authToken) {
        try {
            // Vulnerability: Minimal token validation
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // Vulnerability: Detailed error logging
            logger.error("Invalid JWT signature: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: " + ex.getMessage());
        }
        return false;
    }
    
    // Vulnerability: No token blacklisting mechanism
    // Vulnerability: No token refresh mechanism
    // Vulnerability: No role-based token validation
    
    public String generateAdminToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("role", "admin")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                // Vulnerability: Same secret for admin and user tokens
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
