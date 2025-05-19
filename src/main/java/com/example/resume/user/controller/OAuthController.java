package com.example.resume.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private static final String ADDITIONAL_FORM_URL = "http://43.203.221.191//app/wishy/page-682b2a605d83947b1f4c94f5";

    @GetMapping("/success")
    public void oauthSuccess(HttpServletResponse response,
                             @AuthenticationPrincipal OAuth2User principal) throws IOException {
        String email = principal.getAttribute("email");

        String token = JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1일
                .sign(Algorithm.HMAC256(jwtSecret)); // 이 키는 강력하고 안전하게!

        String redirectUrl = ADDITIONAL_FORM_URL+"??token=" + token;

        response.sendRedirect(redirectUrl);
    }
}

