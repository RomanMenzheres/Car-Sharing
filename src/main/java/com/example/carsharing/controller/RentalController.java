package com.example.carsharing.controller;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.User;
import com.example.carsharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@AllArgsConstructor
@Tag(name = "Rental Management", description = "Endpoints for managing rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create Rental", description = "Create a new rental")
    @PostMapping
    public RentalWithDetailedCarInfoDto create(
            @Valid @RequestBody CreateRentalRequestDto requestDto,
            Authentication authentication
    ) {
        return rentalService.save(requestDto, (User) authentication.getPrincipal());
    }

    @Operation(summary = "Get Your Rentals", description = "Get all your rentals")
    @GetMapping("/my")
    public List<RentalDto> getAllByUser(Authentication authentication, Pageable pageable) {
        return rentalService.findAllByUser(
                ((User) authentication.getPrincipal()).getId(), pageable);
    }

    @Operation(summary = "Get User's Rentals",
            description = "Get All active or not rentals by specific user")
    @GetMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<RentalDto> getAllByUserAndActivity(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") boolean isActive,
            Pageable pageable
    ) {
        return rentalService.findAllByUserAndActivity(userId, isActive, pageable);
    }

    @Operation(summary = "Get Rental By Id", description = "Get Rental by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public RentalWithDetailedCarInfoDto getById(@PathVariable("id") Long id) {
        return rentalService.findById(id);
    }

    @Operation(summary = "Return Rental", description = "Return specific rental")
    @PostMapping("/{id}/return")
    public RentalWithDetailedCarInfoDto returnRental(@PathVariable("id") Long id) {
        return rentalService.returnRental(id);
    }
}
