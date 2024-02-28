package com.example.carsharing.service.impl;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.RentalIsNotActiveException;
import com.example.carsharing.mapper.RentalMapper;
import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.RentalRepository;
import com.example.carsharing.service.CarService;
import com.example.carsharing.service.RentalService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarService carService;

    @Override
    public RentalWithDetailedCarInfoDto create(CreateRentalRequestDto requestDto, User user) {
        carService.rentalCar(requestDto.carId());
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setUser(user);
        rental.setRentalDateTime(LocalDateTime.now());
        rentalRepository.save(rental);
        return findById(rental.getId());
    }

    @Override
    public List<RentalDto> findAllBy(Long userId, boolean isActive, Pageable pageable) {
        return rentalRepository.findAll(pageable).stream()
                .filter(rental -> (rental.getActualReturnDateTime() == null) == isActive)
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalWithDetailedCarInfoDto returnRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id: " + id));

        if (rental.getActualReturnDateTime() != null) {
            throw new RentalIsNotActiveException("This rental is already not active");
        }
        carService.returnRentalCar(rental.getCar().getId());
        rental.setActualReturnDateTime(LocalDateTime.now());
        return rentalMapper.toWithDetailedCarInfoDto(rentalRepository.save(rental));
    }

    @Override
    public RentalWithDetailedCarInfoDto findById(Long id) {
        return rentalMapper.toWithDetailedCarInfoDto(rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id " + id)));
    }

    @Override
    public List<RentalDto> findAllByUser(User user, Pageable pageable) {
        return rentalRepository.findAllByUser(user, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }
}
