package com.example.resume.recruit.service;

import com.example.resume.cv.domain.Resume;
import com.example.resume.cv.repository.jpa.ResumeRepository;
import com.example.resume.recruit.domain.Recruit;
import com.example.resume.recruit.dto.RecruitCreateRequestDto;
import com.example.resume.recruit.repository.RecruitRepository;
import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;

    public void enrollRecruit(Long resumeId, Long memberId, RecruitCreateRequestDto recruitCreateRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다 : " + memberId));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서가 없습니다 : " + resumeId));
        recruitRepository.save(Recruit.builder()
                .member(member)
                .resume(resume)
                .companyName(recruitCreateRequestDto.companyName())
                .recruitUrl(recruitCreateRequestDto.recruitUrl())
                .build());
    }
}
