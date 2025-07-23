package com.example.resume.user.controller;

import com.example.resume.common.jwt.JwtUtil;
import com.example.resume.token.domain.RefreshToken;
import com.example.resume.token.repository.RefreshTokenRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @GetMapping("/success")
    public void oauthSuccess(HttpServletResponse response,
                             @AuthenticationPrincipal OAuth2User principal) throws IOException {
        Member member = memberService.getUser(principal);
        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);

        memberService.setRefreshToken(member.getId(), refreshToken);
        String redirectUrl = memberService.getRedirectUrl(response, member) + "?token=" + accessToken+"&refreshToken="+refreshToken;
        response.sendRedirect(redirectUrl);
    }
}

