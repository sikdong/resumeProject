package com.example.resume.common;


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
        String id = (String) authentication.getPrincipal();
        return Long.valueOf(id);
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