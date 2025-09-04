package com.example.resume.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEvent {
    private String toEmail;
    private String resumeTitle;
}
