package com.example.carsharing.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "value must be any of enum {enumClass.getEnumConstants()}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
