package com.fastconnect.enums;

public enum FlagStatus {
    OPEN,        // Newly reported, awaiting review
    REVIEWING,   // Moderator is currently looking at it
    RESOLVED     // Action has been taken and the report is closed
}