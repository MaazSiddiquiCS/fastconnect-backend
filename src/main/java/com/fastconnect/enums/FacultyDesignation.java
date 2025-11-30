package com.fastconnect.enums;

public enum FacultyDesignation {
    // Academic Hierarchy
    DIRECTOR(false),
    HOD(true), // Head of Department (HOD)
    HOS(true), // Head of Section (HOS)
    ASSISTANT_DIRECTOR(false),
    LECTURER(true),
    VISITING_FACULTY(true),

    // Supporting/Technical Roles
    LAB_INSTRUCTOR(true),
    MANAGER_ACADEMICS(false),
    MANAGER_ACCOUNTS(false),
    OTHER(false); // For any roles not strictly defined

    private final boolean requiresDepartment;

    FacultyDesignation(boolean requiresDepartment) {
        this.requiresDepartment = requiresDepartment;
    }

    public boolean requiresDepartment() {
        return requiresDepartment;
    }
}
