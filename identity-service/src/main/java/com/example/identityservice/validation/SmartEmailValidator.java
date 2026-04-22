package com.example.identityservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SmartEmailValidator implements ConstraintValidator<SmartEmailConstraint, String> {
    private static final String REQUIRED_DOMAIN = "@smartosc.com";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; //
        }
        return email.toLowerCase().endsWith(REQUIRED_DOMAIN);
    }
}
