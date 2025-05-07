package com.example.resume.dto;

public record UserDto(
        Long id,
        String email,
        String name,
        String profileImage
) {}