package com.example.carsharing.dto.user;

import com.example.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword")
public record UserRegistrationRequestDto(
        @NotNull
        @Length(min = 2, max = 255)
        String firstName,
        @NotNull
        @Length(min = 2, max = 255)
        String lastName,
        @NotNull
        @Length(min = 2, max = 255)
        @Email
        String email,
        @NotNull
        @Length(min = 8, max = 255)
        String password,
        @NotNull
        @Length(min = 8, max = 255)
        String repeatPassword
) {}
