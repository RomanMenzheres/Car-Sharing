package com.example.carsharing.dto.rental;

import java.time.LocalDateTime;

public record RentalDto(
        Long id,
        Long userId,
        Long carId,
        LocalDateTime rentalDateTime,
        LocalDateTime returnDateTime,
        LocalDateTime actualReturnDateTime
) {}
