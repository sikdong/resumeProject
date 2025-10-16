package com.example.resume.user.controller;

import com.example.resume.user.dto.MemberDto;
import com.example.resume.user.dto.MemberLoginRequest;
import com.example.resume.user.dto.MemberRegistrationRequest;
import com.example.resume.user.service.SessionAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SessionAuthController {

    private final SessionAuthService sessionAuthService;

    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@Valid @RequestBody MemberRegistrationRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse httpResponse) {
        MemberDto member = sessionAuthService.register(request, httpRequest, httpResponse);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@Valid @RequestBody MemberLoginRequest request,
                                           HttpServletRequest httpRequest,
                                           HttpServletResponse httpResponse) {
        MemberDto member = sessionAuthService.login(request, httpRequest, httpResponse);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest,
                                       HttpServletResponse httpResponse) {
        sessionAuthService.logout(httpRequest, httpResponse);
        return ResponseEntity.noContent().build();
    }
}
