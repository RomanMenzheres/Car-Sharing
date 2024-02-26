package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserLoginRequestDto;
import com.example.carsharing.dto.user.UserLoginResponseDto;
import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserDto registration(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.save(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
