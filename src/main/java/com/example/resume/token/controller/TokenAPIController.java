package com.example.resume.token.controller;

import com.example.resume.common.jwt.JwtUtil;
import com.example.resume.token.domain.RefreshToken;
import com.example.resume.token.dto.TokenDto;
import com.example.resume.token.repository.RefreshTokenRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenAPIController {
    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshAccessToken(
            @RequestBody TokenDto tokenDto) {

        RefreshToken entity = refreshTokenRepository.findByToken(tokenDto.token())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 Refresh Token"));

        Member member = memberService.findById(entity.getMemberId());
        String newAccessToken = jwtUtil.createAccessToken(member);

        return ResponseEntity.ok(new TokenDto(newAccessToken));
    }
}
