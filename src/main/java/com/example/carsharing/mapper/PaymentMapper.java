package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Rental;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    Payment toModel(CreatePaymentRequestDto requestDto);

    @AfterMapping
    default void setRental(@MappingTarget Payment payment, CreatePaymentRequestDto requestDto) {
        payment.setRental(new Rental(requestDto.rentalId()));
    }

    @Mapping(source = "rental.id", target = "rentalId")
    PaymentDto toDto(Payment payment);
}
