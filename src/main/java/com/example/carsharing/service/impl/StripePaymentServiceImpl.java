package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.RentalIsAlreadyPaid;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Rental;
import com.example.carsharing.repository.PaymentRepository;
import com.example.carsharing.repository.RentalRepository;
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.PaymentService;
import com.example.carsharing.service.RentalService;
import com.example.carsharing.util.StripeUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final StripeUtil stripeUtil;
    private final NotificationService notificationService;

    @Override
    public List<PaymentDto> findByUser(Long userId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getRental().getUser().getId().equals(userId))
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentDto create(CreatePaymentRequestDto requestDto) {
        Long rentalId = requestDto.rentalId();
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find rental with id " + rentalId)
                );
        if (paymentRepository.findByRentalId(rentalId).isPresent()) {
            throw new RentalIsAlreadyPaid("Rental is already paid!");
        }

        Payment payment = paymentMapper.toModel(requestDto);
        payment.setRental(rental);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmountToPay(rentalService.getAmountToPay(rental));
        try {
            Session session = stripeUtil.createStripeSession(
                    payment.getAmountToPay(), "Rental Payment"
            );
            payment.setSessionId(session.getId());
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (StripeException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        PaymentDto paymentDto = paymentMapper.toDto(paymentRepository.save(payment));

        notificationService.onPaymentCreationNotification(paymentDto);

        return paymentDto;
    }

    @Override
    public PaymentDto successful(String sessionId) {
        Payment payment = findBySessionId(sessionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        PaymentDto paymentDto = paymentMapper.toDto(paymentRepository.save(payment));
        notificationService.onSuccessfulPayment(paymentDto);
        return paymentDto;
    }

    @Override
    public PaymentDto canceled(String sessionId) {
        Payment payment = findBySessionId(sessionId);
        payment.setStatus(Payment.PaymentStatus.CANCELED);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    private Payment findBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find payment by session id: " + sessionId));
    }
}
