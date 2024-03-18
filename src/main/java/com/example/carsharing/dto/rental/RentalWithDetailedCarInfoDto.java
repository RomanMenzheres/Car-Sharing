package com.example.carsharing.dto.rental;

import com.example.carsharing.dto.car.CarDto;
import java.time.LocalDate;

public record RentalWithDetailedCarInfoDto(
        Long id,
        Long userId,
        CarDto car,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate
) {}
