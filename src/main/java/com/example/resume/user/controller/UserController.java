package com.example.resume.user.controller;

import com.example.resume.common.MemberUtil;
import com.example.resume.common.auth.CustomUserDetails;
import com.example.resume.user.dto.UserAdditionalInfoRequestDto;
import com.example.resume.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final MemberService memberService;

    @PostMapping("/additional-info")
    public ResponseEntity<?> updateInfo(@RequestBody UserAdditionalInfoRequestDto request,
                                        Authentication authentication){
        Long memberId = MemberUtil.getMemberId(authentication);
        memberService.update(request, memberId);
        return ResponseEntity.ok().build();
    }
}
