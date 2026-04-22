package com.example.identityservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SmartEmailValidator.class)
public @interface SmartEmailConstraint {
    String message() default "INVALID_EMAIL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
