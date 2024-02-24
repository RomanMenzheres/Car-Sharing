package com.example.carsharing.dto.car;

import com.example.carsharing.model.Car;
import com.example.carsharing.validation.ValueOfEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateCarRequestDto {
    @NotEmpty
    @Length(max = 255)
    private String brand;
    @NotEmpty
    @Length(max = 255)
    private String model;
    @ValueOfEnum(enumClass = Car.CarType.class)
    private String type;
    @Min(0)
    private int inventory;
    @DecimalMin("0")
    private BigDecimal dailyFee;
}
