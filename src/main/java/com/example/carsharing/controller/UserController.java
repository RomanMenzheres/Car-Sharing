package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserDto;
import com.example.carsharing.dto.user.UserUpdateRequestDto;
import com.example.carsharing.dto.user.UserWithRoleDto;
import com.example.carsharing.model.User;
import com.example.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get Your Info", description = "Get the information about you")
    @GetMapping("/me")
    public UserDto get(Authentication authentication) {
        return userService.findById(((User) authentication.getPrincipal()).getId());
    }

    @Operation(summary = "Change User's Role", description = "Change user's role")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    public UserWithRoleDto updateRole(@PathVariable("id") Long id, @RequestParam User.Role role) {
        return userService.updateRole(id, role);
    }

    @Operation(summary = "Update Your Info", description = "Update the information about you")
    @PatchMapping("/me")
    public UserDto update(Authentication authentication,
                          @Valid @RequestBody UserUpdateRequestDto requestDto) {
        return userService.update(((User) authentication.getPrincipal()), requestDto);
    }
}
