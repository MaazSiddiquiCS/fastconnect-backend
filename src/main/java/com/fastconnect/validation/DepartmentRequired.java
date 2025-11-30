package com.fastconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DepartmentValidator.class})
public @interface DepartmentRequired {
    String message() default "Department required for academic designation";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
