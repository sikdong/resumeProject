package com.example.resume.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.resume.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final long expirationTime = 1000 * 60 * 60;

    private final Algorithm algorithm;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key is missing!");
        }
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String createToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withClaim("name", user.getName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getVerifier().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        DecodedJWT jwt = getVerifier().verify(token);
        return jwt.getClaim("id").asLong();
    }

    private DecodedJWT decodeToken(String token) {
        return getVerifier().verify(token);
    }

    private JWTVerifier getVerifier() {
        return JWT.require(algorithm).build();
    }
}


