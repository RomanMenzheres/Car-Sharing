package com.example.carsharing.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateRentalRequestDto(
        @Positive
        Long carId,
        @Future
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate returnDate
) {}
