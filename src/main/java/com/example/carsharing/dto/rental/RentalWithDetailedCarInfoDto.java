package com.example.carsharing.dto.rental;

import com.example.carsharing.dto.car.CarDto;
import java.time.LocalDateTime;

public record RentalWithDetailedCarInfoDto(
        Long id,
        Long userId,
        CarDto car,
        LocalDateTime rentalDateTime,
        LocalDateTime returnDateTime,
        LocalDateTime actualReturnDateTime
) {}
