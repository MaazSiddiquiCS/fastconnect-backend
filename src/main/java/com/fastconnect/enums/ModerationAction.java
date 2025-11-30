package com.fastconnect.enums;

public enum ModerationAction {
    // Destructive Actions
    CONTENT_DELETED,
    CONTENT_ARCHIVED,
    USER_SUSPENDED,
    USER_BANNED,

    // Non-Destructive Actions
    WARNING_ISSUED,
    NO_ACTION_TAKEN,    // Content was reviewed and deemed acceptable
    ESCALATED           // Action deferred to a higher-level team
}