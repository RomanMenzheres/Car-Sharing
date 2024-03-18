package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.Car;
import com.example.carsharing.model.Rental;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = CarMapper.class)
public interface RentalMapper {
    Rental toModel(CreateRentalRequestDto requestDto);

    @AfterMapping
    default void setCar(@MappingTarget Rental rental, CreateRentalRequestDto requestDto) {
        rental.setCar(new Car(requestDto.carId()));
    }

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "car.id", target = "carId")
    RentalDto toDto(Rental rental);

    @Mapping(source = "user.id", target = "userId")
    RentalWithDetailedCarInfoDto toWithDetailedCarInfoDto(Rental rental);
}
