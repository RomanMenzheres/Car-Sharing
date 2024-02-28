package com.example.carsharing.service;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalWithDetailedCarInfoDto create(CreateRentalRequestDto requestDto, User user);

    List<RentalDto> findAllBy(Long userId, boolean isActive, Pageable pageable);

    RentalWithDetailedCarInfoDto returnRental(Long id);

    RentalWithDetailedCarInfoDto findById(Long id);

    List<RentalDto> findAllByUser(User user, Pageable pageable);
}
