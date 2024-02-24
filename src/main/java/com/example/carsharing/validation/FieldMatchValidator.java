package com.example.carsharing.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String first;
    private String second;

    public void initialize(FieldMatch constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        Object firstFieldValue = new BeanWrapperImpl(object)
                .getPropertyValue(first);
        Object secondFieldValue = new BeanWrapperImpl(object)
                .getPropertyValue(second);

        return (firstFieldValue != null && firstFieldValue.equals(secondFieldValue))
                || (firstFieldValue == null && firstFieldValue == secondFieldValue);
    }
}
