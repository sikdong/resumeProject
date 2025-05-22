package com.example.resume.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.resume.common.jwt.JwtUtil;
import com.example.resume.user.domain.User;
import com.example.resume.user.dto.UserAdditionalInfoRequestDto;
import com.example.resume.user.repository.UserRepository;
import com.example.resume.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/success")
    public void oauthSuccess(HttpServletResponse response,
                             @AuthenticationPrincipal OAuth2User principal) throws IOException {
        User user = userService.getUser(principal);
        String redirectUrl = userService.getRedirectUrl(response, user);

        String token = jwtUtil.createToken(user);
        redirectUrl += "?token=" + token;

        response.sendRedirect(redirectUrl);
    }
}

