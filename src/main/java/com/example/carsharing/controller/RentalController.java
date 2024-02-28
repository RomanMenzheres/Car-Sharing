package com.example.carsharing.controller;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.User;
import com.example.carsharing.service.RentalService;
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
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public RentalWithDetailedCarInfoDto create(
            @Valid @RequestBody CreateRentalRequestDto requestDto,
            Authentication authentication
    ) {
        return rentalService.create(requestDto, (User) authentication.getPrincipal());
    }

    @GetMapping("/my")
    public List<RentalDto> getAllByUser(Authentication authentication, Pageable pageable) {
        return rentalService.findAllByUser((User) authentication.getPrincipal(), pageable);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<RentalDto> getAllByUserAndActivity(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") boolean isActive,
            Pageable pageable
    ) {
        return rentalService.findAllBy(userId, isActive, pageable);
    }

    @GetMapping("/{id}")
    public RentalWithDetailedCarInfoDto getById(@PathVariable("id") Long id) {
        return rentalService.findById(id);
    }

    @PostMapping("/{id}/return")
    public RentalWithDetailedCarInfoDto returnRental(@PathVariable("id") Long id) {
        return rentalService.returnRental(id);
    }
}
