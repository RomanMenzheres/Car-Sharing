package com.example.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CarWithShortInfoDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.NoAvailableCarsException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.repository.CarRepository;
import com.example.carsharing.service.impl.CarServiceImpl;
import com.example.carsharing.supplier.CarSupplier;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateRequestDto_ReturnsCarDto() {
        CreateCarRequestDto requestDto = CarSupplier.getCreateCarRequestDto();
        CarDto expected = CarSupplier.getCarDto();
        Car car = CarSupplier.getCar();

        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.save(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(carRepository, times(1)).save(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnsAllCars() {
        Car car = CarSupplier.getCar();
        CarWithShortInfoDto carWithShortInfoDto = CarSupplier.getCarWithShortInfoDto();
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> cars = List.of(car);
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toWithShortInfoDto(car)).thenReturn(carWithShortInfoDto);

        List<CarWithShortInfoDto> actual = carService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(carWithShortInfoDto);
        verify(carRepository, times(1)).findAll(pageable);
        verify(carMapper, times(1)).toWithShortInfoDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify the correct car was returned when car exists")
    public void findById_ValidId_ReturnsCarDto() {
        Long carId = 4L;
        Car car = CarSupplier.getCar();
        CarDto expected = CarSupplier.getCarDto();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.findById(carId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(carId);
        verify(carMapper, times(1)).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify the exception was thrown when invalid id")
    public void findById_InvalidId_ThrowsException() {
        Long carId = -1L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.findById(carId)
        );

        String expected = "Can't find car with id: " + carId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify the correct inventory of car was changed after rental")
    public void rentalCar_ValidData_DecreaseInventory() {
        Car car = CarSupplier.getCar();
        int expected = car.getInventory() - 1;

        carService.rentalCar(car);

        assertEquals(expected, car.getInventory());
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify the exception was thrown when car inventory is 0")
    public void rentalCar_ZeroInventory_ThrowsException() {
        Car car = CarSupplier.getCar();
        car.setInventory(0);

        NoAvailableCarsException exception = assertThrows(
                NoAvailableCarsException.class,
                () -> carService.rentalCar(car)
        );

        String expected = "There no available cars with id: " + car.getId() + " at the moment";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify the correct inventory of car was changed after return")
    public void returnCar_ValidData_IncreaseInventory() {
        Car car = CarSupplier.getCar();
        int expected = car.getInventory() + 1;

        carService.returnRentalCar(car);

        assertEquals(expected, car.getInventory());
        verifyNoMoreInteractions(carRepository, carMapper);
    }
}
