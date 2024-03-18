package com.example.carsharing.supplier;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CarWithShortInfoDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.model.Car;
import java.math.BigDecimal;

public class CarSupplier {

    public static CreateCarRequestDto getCreateCarRequestDto() {
        return new CreateCarRequestDto(
                "BYD",
                "Song L",
                "SUV",
                5,
                BigDecimal.valueOf(700)
        );
    }

    public static CarDto getCarDto() {
        return new CarDto(
                4L,
                "BYD",
                "Song L",
                "SUV",
                5,
                BigDecimal.valueOf(700)
        );
    }

    public static CreateCarRequestDto getInvalidCreateCarRequestDto() {
        return new CreateCarRequestDto(
                "",
                "",
                "",
                -1,
                BigDecimal.valueOf(-700)
        );
    }

    public static CarDto getCarWithId2() {
        return new CarDto(
                2L,
                "Zeekr",
                "001",
                "SUV",
                5,
                BigDecimal.valueOf(750)
        );
    }

    public static CreateCarRequestDto getUpdatedCreateCarRequestDto() {
        return new CreateCarRequestDto(
                "Zeekr",
                "001",
                "SUV",
                7,
                BigDecimal.valueOf(1200)
        );
    }

    public static CarDto getUpdatedCarDto() {
        return new CarDto(
                2L,
                "Zeekr",
                "001",
                "SUV",
                7,
                BigDecimal.valueOf(1200)
        );
    }

    public static Car getCar() {
        Car car = new Car();
        car.setId(4L);
        car.setBrand("BYD");
        car.setModel("Song L");
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(700));
        car.setType(Car.CarType.SUV);
        return car;
    }

    public static CarWithShortInfoDto getCarWithShortInfoDto() {
        return new CarWithShortInfoDto(
                4L,
                "BYD",
                "Song L",
                "SUV"
        );
    }
}
