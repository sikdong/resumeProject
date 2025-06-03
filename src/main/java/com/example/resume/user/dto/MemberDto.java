package com.example.resume.user.dto;

import com.example.resume.user.domain.Member;

public record MemberDto(
        Long id,
        String email,
        String name,
        String careerLevel,
        String jobTitle
) {
    public static MemberDto fromEntity(Member member){
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getCareerLevel().getLabel(),
                member.getJobTitle()
        );
    }
}