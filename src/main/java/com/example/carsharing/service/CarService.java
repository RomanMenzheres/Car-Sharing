package com.example.carsharing.service;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CarWithShortInfoDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.model.Car;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    List<CarWithShortInfoDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    CarDto update(Long id, CreateCarRequestDto requestDto);

    void delete(Long id);

    void rentalCar(Car car);

    void returnRentalCar(Car car);
}
