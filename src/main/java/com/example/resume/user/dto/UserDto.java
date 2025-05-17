package com.example.resume.user.dto;

public record UserDto(
        Long id,
        String email,
        String name,
        String profileImage
) {}