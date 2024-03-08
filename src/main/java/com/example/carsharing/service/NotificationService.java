package com.example.carsharing.service;

import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import java.util.List;
import java.util.Map;

public interface NotificationService {
    void onRentalCreationNotification(RentalWithDetailedCarInfoDto rental);

    void scheduledOverdueRentalNotification(Map<Long, List<RentalDto>> overdueRentals);

    void onPaymentCreationNotification(PaymentDto paymentDto, Long userId);

    void onSuccessfulPayment(PaymentDto paymentDto, Long userId);
}
