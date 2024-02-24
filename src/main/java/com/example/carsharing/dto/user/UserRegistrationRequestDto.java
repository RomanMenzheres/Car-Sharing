package com.example.carsharing.dto.user;

import com.example.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotNull
    @Length(min = 2, max = 255)
    private String firstName;
    @NotNull
    @Length(min = 2, max = 255)
    private String lastName;
    @NotNull
    @Length(min = 2, max = 255)
    @Email
    private String email;
    @NotNull
    @Length(min = 8, max = 255)
    private String password;
    @NotNull
    @Length(min = 8, max = 255)
    private String repeatPassword;
}
