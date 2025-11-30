package com.fastconnect.enums;

public enum ActionType {
    // User Lifecycle
    USER_CREATED,
    PROFILE_UPDATED,
    PASSWORD_RESET,
    ACCOUNT_SUSPENDED,

    // Content Actions
    POST_CREATED,
    POST_EDITED,
    POST_DELETED,

    // Society/Faculty Actions
    SOCIETY_APPROVED,
    FACULTY_PAGE_CREATED,

    // Admin/Moderation Actions
    MODERATION_ACTION_TAKEN,
    ADMIN_LOGIN
}
