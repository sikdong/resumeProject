package com.example.resume.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.resume.user.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    private final Duration accessExpirationTime = Duration.ofHours(3);
    private final Duration refreshExpirationTime = Duration.ofDays(7);

    private final Algorithm algorithm;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key is missing!");
        }
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String createAccessToken(Member member) {
        return JWT.create()
                .withSubject(member.getEmail())
                .withClaim("id", member.getId().toString())
                .withClaim("name", member.getName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpirationTime.toMillis()))
                .sign(algorithm);
    }

    public String createRefreshToken(Member member) {
        return JWT.create()
                .withSubject(member.getId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationTime.toMillis()))
                .sign(algorithm);
    }

    public String extractMemberId(String token) {
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

    public String getUserId(String token) {
        DecodedJWT jwt = getVerifier().verify(token);
        Claim idClaim = jwt.getClaim("id");
        if (idClaim.asString() != null) {
            return idClaim.asString();
        } else if (idClaim.asInt() != null) {
            return String.valueOf(idClaim.asInt());
        }
        return null;
    }

    private DecodedJWT decodeToken(String token) {
        return getVerifier().verify(token);
    }

    private JWTVerifier getVerifier() {
        return JWT.require(algorithm).build();
    }
}


