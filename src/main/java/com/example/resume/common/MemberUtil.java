package com.example.resume.common;


import org.springframework.security.core.Authentication;

public class MemberUtil {

    public static Long getMemberId(Authentication authentication){
        String id = (String)authentication.getPrincipal();
        return Long.valueOf(id);
    }
}
