package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserUpdateRequestDto;
import com.example.carsharing.dto.user.UserWithRoleDto;
import com.example.carsharing.model.User;
import com.example.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto get(Authentication authentication) {
        return userService.findById(((User) authentication.getPrincipal()).getId());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    public UserWithRoleDto updateRole(@PathVariable("id") Long id, @RequestParam User.Role role) {
        return userService.updateRole(id, role);
    }

    @PatchMapping("/me")
    public UserDto update(Authentication authentication,
                          @Valid @RequestBody UserUpdateRequestDto requestDto) {
        return userService.update(((User) authentication.getPrincipal()), requestDto);
    }
}
