package com.example.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @Min(8)
    private String password;
}
