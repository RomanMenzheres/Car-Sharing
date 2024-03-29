package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CarWithShortInfoDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.model.Car;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarRequestDto requestDto);

    CarWithShortInfoDto toWithShortInfoDto(Car car);
}
