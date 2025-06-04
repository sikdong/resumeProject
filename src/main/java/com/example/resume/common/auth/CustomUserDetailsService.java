package com.example.resume.common.auth;

import com.example.resume.user.domain.Member;
import com.example.resume.user.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository repo) {
        this.memberRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(member);
    }
}

