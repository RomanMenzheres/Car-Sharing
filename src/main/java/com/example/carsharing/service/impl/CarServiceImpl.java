package com.example.carsharing.service.impl;

import com.example.carsharing.dto.CarDto;
import com.example.carsharing.dto.CarDtoWithShortInfo;
import com.example.carsharing.dto.CreateCarRequestDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.repository.CarRepository;
import com.example.carsharing.service.CarService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        return carMapper.toDto(carRepository.save(carMapper.toModel(requestDto)));
    }

    @Override
    public List<CarDtoWithShortInfo> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDtoWithShortInfo)
                .collect(Collectors.toList());
    }

    @Override
    public CarDto findById(Long id) {
        return carMapper.toDto(
                carRepository.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Can't find car with id: " + id)
                        )
        );
    }

    @Override
    public CarDto update(Long id, CreateCarRequestDto requestDto) {
        findById(id);
        Car car = carMapper.toModel(requestDto);
        car.setId(id);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void delete(Long id) {
        carRepository.deleteById(id);
    }
}
