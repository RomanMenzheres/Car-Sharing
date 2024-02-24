package com.example.carsharing.service.impl;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserUpdateRequestDto;
import com.example.carsharing.dto.user.UserWithRoleDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.RegistrationException;
import com.example.carsharing.mapper.UserMapper;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.UserRepository;
import com.example.carsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto save(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Provided email is already taken");
        }
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User user = userMapper.toModel(requestDto);
        user.setRole(User.Role.CUSTOMER);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(Long id) {
        return userMapper.toDto(
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Can't find user with id: " + id)
                        )
        );
    }

    @Override
    public UserDto update(User user, UserUpdateRequestDto requestDto) {
        userMapper.updateUserFromDto(requestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserWithRoleDto updateRole(Long id, User.Role role) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find user with id: " + id)
                );
        user.setRole(role);
        return userMapper.toWithRoleDto(userRepository.save(user));
    }
}
