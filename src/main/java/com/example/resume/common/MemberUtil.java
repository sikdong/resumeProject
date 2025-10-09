package com.example.resume.common;


import com.example.resume.common.auth.CustomUserDetails;
import com.example.resume.user.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.stream.Stream;

public class MemberUtil {
    private static final String UNKNOWN = "unknown";
    private static final String[] IP_HEADERS = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP"
    };

    public static Long getMemberId(Authentication authentication) {
        if (authentication == null) {
            return 0L;
        }

        Object principalObject = authentication.getPrincipal();

        if (principalObject instanceof CustomUserDetails customUserDetails) {
            Member member = customUserDetails.member();
            return member != null ? member.getId() : 0L;
        }

        if (principalObject instanceof Member member) {
            return member.getId();
        }

        String principal = String.valueOf(principalObject);
        if (principal.isBlank() || "anonymousUser".equalsIgnoreCase(principal)) {
            return 0L;
        }

        try {
            return Long.parseLong(principal);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = Stream.of(IP_HEADERS)
            .map(request::getHeader)
            .filter(MemberUtil::isValidIp)
            .findFirst()
            .orElse(request.getRemoteAddr());

        return extractFirstIp(ip);
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static String extractFirstIp(String ip) {
        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0];
        }
        return ip;
    }
}
