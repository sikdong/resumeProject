package com.example.resume.user.service;

import com.example.resume.common.session.SessionConstants;
import com.example.resume.common.session.SessionUser;
import com.example.resume.user.domain.Member;
import com.example.resume.user.dto.MemberDto;
import com.example.resume.user.dto.MemberLoginRequest;
import com.example.resume.user.dto.MemberRegistrationRequest;
import com.example.resume.user.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDto register(MemberRegistrationRequest request,
                              HttpServletRequest httpRequest,
                              HttpServletResponse httpResponse) {
        memberRepository.findByEmail(request.email()).ifPresent(member -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        });

        Member member = memberRepository.save(Member.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .provider("local")
                .role(Member.Role.USER)
                .build());

        establishSession(member, httpRequest, httpResponse);
        return MemberDto.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public MemberDto login(MemberLoginRequest request,
                           HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "등록되지 않은 이메일입니다."));

        if (member.getPassword() == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        establishSession(member, httpRequest, httpResponse);
        return MemberDto.fromEntity(member);
    }

    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        boolean secureRequest = httpRequest.isSecure();
        ResponseCookie deleteCookie = ResponseCookie.from(SessionConstants.MEMBER_COOKIE_NAME, "")
                .path("/")
                .maxAge(0)
                .sameSite(secureRequest ? "None" : "Lax")
                .httpOnly(false)
                .secure(secureRequest)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        SecurityContextHolder.clearContext();
    }

    private void establishSession(Member member,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        SessionUser sessionUser = new SessionUser(member.getId(), member.getEmail(), member.getName(), member.getRole());
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionConstants.SESSION_USER_KEY, sessionUser);
        session.setMaxInactiveInterval((int) Duration.ofHours(2).toSeconds());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                sessionUser.memberId().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + sessionUser.role().name()))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        String displayName = sessionUser.name() != null ? sessionUser.name() : "";
        String encodedName = URLEncoder.encode(displayName, StandardCharsets.UTF_8);

        makeCustomCookie(request, response, encodedName);
    }

    private static void makeCustomCookie(HttpServletRequest request, HttpServletResponse response, String encodedName) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstants.MEMBER_COOKIE_NAME, encodedName)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .httpOnly(false)
                .secure(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
