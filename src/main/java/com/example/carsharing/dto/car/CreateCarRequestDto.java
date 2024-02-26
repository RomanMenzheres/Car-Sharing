package com.example.carsharing.dto.car;

import com.example.carsharing.model.Car;
import com.example.carsharing.validation.ValueOfEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Length;

public record CreateCarRequestDto(
        @NotEmpty
        @Length(max = 255)
        String brand,
        @NotEmpty
        @Length(max = 255)
        String model,
        @ValueOfEnum(enumClass = Car.CarType.class)
        String type,
        @Min(0)
        int inventory,
        @DecimalMin("0")
        BigDecimal dailyFee
) {}
