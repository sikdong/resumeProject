package com.example.resume.user.service;

import com.example.resume.enums.CareerLevel;
import com.example.resume.user.domain.User;
import com.example.resume.user.dto.UserAdditionalInfoRequestDto;
import com.example.resume.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final String ADDITIONAL_FORM_URL = "http://43.203.221.191/app/wishy/page-682b2a605d83947b1f4c94f5/edit";
    private static final String MAIN_PAGE = "http://43.203.221.191/app/wishy/page-682b30db5d83947b1f4c94f9/edit";

    public String getRedirectUrl(HttpServletResponse response, User user) {
        CareerLevel careerLevel = user.getCareerLevel();
        String jobTitle = user.getJobTitle();
        if (careerLevel == null && StringUtils.isEmpty(jobTitle)){
            return ADDITIONAL_FORM_URL;
        }
        return MAIN_PAGE;
    }

    public User getUser(OAuth2User principal) {
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email)
                .orElseGet(()->userRepository.save(User.builder()
                        .name(principal.getAttribute("name"))
                        .provider("google")
                        .email(email)
                        .build()));
    }

    public void update(UserAdditionalInfoRequestDto request, HttpServletResponse response, String username) {

    }
}
