package com.example.carsharing.dto.rental;

import java.time.LocalDate;

public record RentalDto(
        Long id,
        Long userId,
        Long carId,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate
) {}
