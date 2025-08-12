package com.example.resume.recruit.controller;

import com.example.resume.common.MemberUtil;
import com.example.resume.recruit.dto.RecruitCreateRequestDto;
import com.example.resume.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit")
public class RecruitController {
    private final RecruitService recruitService;

    @PostMapping("/{resumeId}")
    public ResponseEntity<String> createRecruit(
            @PathVariable Long resumeId,
            @RequestBody RecruitCreateRequestDto recruitCreateRequestDto,
            Authentication authentication
    ){
        Long memberId = MemberUtil.getMemberId(authentication);
        recruitService.enrollRecruit(resumeId, memberId, recruitCreateRequestDto);
        return ResponseEntity.ok("합격 정보를 등록했습니다. 합격을 축하합니다!");
    }

}
