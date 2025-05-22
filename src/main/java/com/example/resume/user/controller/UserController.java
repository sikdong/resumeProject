package com.example.resume.user.controller;

import com.example.resume.common.auth.CustomUserDetails;
import com.example.resume.user.dto.UserAdditionalInfoRequestDto;
import com.example.resume.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/additional-info")
    public ResponseEntity<?> updateInfo(@RequestBody UserAdditionalInfoRequestDto request,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.user().getId();
        userService.update(request, userId);
        return ResponseEntity.ok().build();
    }
}
