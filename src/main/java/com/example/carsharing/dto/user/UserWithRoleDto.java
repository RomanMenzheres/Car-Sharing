package com.example.carsharing.dto.user;

import com.example.carsharing.model.User;

public record UserWithRoleDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        User.Role role
){}
