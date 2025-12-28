package com.fastconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NUEmailValidator implements ConstraintValidator<ValidNUEmail, String> {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%-]+@nu\\.edu\\.pk$";
    private Pattern pattern;

    @Override
    public void initialize(ValidNUEmail constraintAnnotation) {
        this.pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Allow null/empty string to be handled by @NotBlank instead
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}