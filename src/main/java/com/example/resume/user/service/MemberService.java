package com.example.resume.user.service;

import com.example.resume.enums.CareerLevel;
import com.example.resume.token.domain.RefreshToken;
import com.example.resume.token.repository.RefreshTokenRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.dto.UserAdditionalInfoRequestDto;
import com.example.resume.user.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${app.url.additional-form}")
    private String additionalFormUrl;

    @Value("${app.url.main-page}")
    private String mainPageUrl;

    public String getRedirectUrl(HttpServletResponse response, Member member) {
        CareerLevel careerLevel = member.getCareerLevel();
        String jobTitle = member.getJobTitle();
        if (careerLevel == null && StringUtils.isEmpty(jobTitle)){
            return additionalFormUrl;
        }
        return mainPageUrl;
    }

    public Member getUser(OAuth2User principal) {
        String email = principal.getAttribute("email");
        return memberRepository.findByEmail(email)
                .orElseGet(()-> memberRepository.save(Member.builder()
                        .name(principal.getAttribute("name"))
                        .provider("google")
                        .role(Member.Role.USER)
                        .email(email)
                        .build()));
    }
    @Transactional
    public void update(UserAdditionalInfoRequestDto request, Long userId) {
        Member member = getMember(userId);
        member.setCareerLevel(CareerLevel.valueOf(request.careerLevel()));
        member.setJobTitle(request.jobTitle());
    }

    public Member findById(Long userId) {
        return getMember(userId);
    }

    private Member getMember(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " not found"));
    }

    @Transactional
    public void setRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.findById(memberId)
            .ifPresentOrElse(token -> token.setToken(refreshToken),
                    () -> refreshTokenRepository.save(RefreshToken.builder().
                            memberId(memberId).
                            token(refreshToken)
                            .build()));
    }
}
