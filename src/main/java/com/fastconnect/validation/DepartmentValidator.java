package com.fastconnect.validation;

import com.fastconnect.entity.FacultyPage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DepartmentValidator implements ConstraintValidator<DepartmentRequired, FacultyPage> {

    @Override
    public boolean isValid(FacultyPage facultyPage, ConstraintValidatorContext context) {

        // 1. Get the designation object from the entity
        if (facultyPage == null || facultyPage.getFacultyDesignation() == null) {
            // Let @NotNull handle this error separately, or simply pass validation if object is null.
            return true;
        }

        // 2. Check the business rule using the enum's built-in logic
        if (facultyPage.getFacultyDesignation().requiresDepartment()) {

            // If the role requires a department, check if the department field is null.
            if (facultyPage.getDepartment() == null) {
                // Manually attach the error message to the 'department' field for good UX.
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("department")
                        .addConstraintViolation();
                return false; // Validation Fails
            }
        }

        // 3. If the role doesn't require a department, or if it does and the department is present, return true.
        return true;
    }
}