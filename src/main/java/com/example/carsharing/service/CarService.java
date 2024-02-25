package com.example.carsharing.service;

import com.example.carsharing.dto.CarDto;
import com.example.carsharing.dto.CarWithShortInfoDto;
import com.example.carsharing.dto.CreateCarRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    List<CarWithShortInfoDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    CarDto update(Long id, CreateCarRequestDto requestDto);

    void delete(Long id);
}
