package com.fastconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NUEmailValidator.class) // Links to the implementation class
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Can be used on fields and method parameters
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNUEmail {

    // Default error message if validation fails
    String message() default "Email domain must be @nu.edu.pk";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}