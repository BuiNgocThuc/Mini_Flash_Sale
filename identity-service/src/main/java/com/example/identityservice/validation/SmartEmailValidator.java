package com.example.identityservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SmartEmailValidator implements ConstraintValidator<SmartEmailConstraint, String> {
    private static final String EMAIL_COM_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_COM_REGEX);

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; //
        }
        return PATTERN.matcher(email).matches();
    }
}
