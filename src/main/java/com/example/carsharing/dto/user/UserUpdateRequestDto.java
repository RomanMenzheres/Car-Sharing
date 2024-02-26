package com.example.carsharing.dto.user;

import com.example.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword")
public record UserUpdateRequestDto(
        @Length(min = 2, max = 255)
        String firstName,
        @Length(min = 2, max = 255)
        String lastName,
        @Email
        @Length(min = 2, max = 255)
        String email,
        @Length(min = 8, max = 255)
        String password,
        @Length(min = 8, max = 255)
        String repeatPassword
) {}
