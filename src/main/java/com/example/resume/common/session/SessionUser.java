package com.example.resume.common.session;

import com.example.resume.user.domain.Member;

public record SessionUser(Long memberId, String email, String name, Member.Role role) {
}
