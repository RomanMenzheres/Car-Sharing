package com.example.carsharing.controller;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CarWithShortInfoDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@AllArgsConstructor
@Tag(name = "Car Management", description = "Endpoints for managing cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Create Car", description = "Create a new car")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MANAGER')")
    public CarDto create(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Get All Cars", description = "Get all available cars")
    @GetMapping
    public List<CarWithShortInfoDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @Operation(summary = "Get Car By Id", description = "Get specific car by id")
    @GetMapping("/{id}")
    public CarDto getById(@PathVariable("id") Long id) {
        return carService.findById(id);
    }

    @Operation(summary = "Update Car", description = "Update the information about car")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public CarDto update(@PathVariable("id") Long id,
                         @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.update(id, requestDto);
    }

    @Operation(summary = "Delete car", description = "Delete specific car by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MANAGER')")
    public void delete(@PathVariable("id") Long id) {
        carService.delete(id);
    }
}
