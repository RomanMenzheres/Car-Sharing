package com.example.carsharing.service;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserUpdateRequestDto;
import com.example.carsharing.dto.user.UserWithRoleDto;
import com.example.carsharing.model.User;

public interface UserService {
    UserDto save(UserRegistrationRequestDto requestDto);

    UserDto findById(Long id);

    UserDto update(User user, UserUpdateRequestDto requestDto);

    UserWithRoleDto updateRole(Long id, User.Role role);
}
