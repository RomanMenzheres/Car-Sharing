package com.example.carsharing.service;

import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import java.util.List;

public interface NotificationService {
    void onRentalCreationNotification(RentalWithDetailedCarInfoDto rental);

    void scheduledOverdueRentalNotification(List<RentalDto> overdueRentals);

    void onPaymentCreationNotification(PaymentDto paymentDto);

    void onSuccessfulPayment(PaymentDto paymentDto);
}
