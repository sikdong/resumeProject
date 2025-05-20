package com.example.resume.enums;

import lombok.Getter;

@Getter
public enum CareerLevel {
    JUNIOR("신입"),
    ASSOCIATE("1~3년차"),
    MID_LEVEL("3~5년차"),
    SENIOR("5년 이상");

    private final String label;

    CareerLevel(String label) {
        this.label = label;
    }

    public static CareerLevel fromLabel(String label) {
        for (CareerLevel level : values()) {
            if (level.label.equals(label)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
