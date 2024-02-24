package com.example.carsharing.dto.user;

import com.example.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserUpdateRequestDto {
    @Length(min = 2, max = 255)
    private String firstName;
    @Length(min = 2, max = 255)
    private String lastName;
    @Email
    @Length(min = 2, max = 255)
    private String email;
    @Length(min = 8, max = 255)
    private String password;
    @Length(min = 8, max = 255)
    private String repeatPassword;
}
