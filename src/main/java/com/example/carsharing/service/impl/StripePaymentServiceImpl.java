package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.PaymentException;
import com.example.carsharing.exception.RentalIsAlreadyPaid;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.Payment;
import com.example.carsharing.repository.PaymentRepository;
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.PaymentService;
import com.example.carsharing.service.RentalService;
import com.example.carsharing.util.StripeUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
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
    public PaymentDto save(CreatePaymentRequestDto requestDto) {
        Long rentalId = requestDto.rentalId();
        RentalWithDetailedCarInfoDto rental = rentalService.findById(rentalId);
        Optional<Payment> byRentalId = paymentRepository.findByRentalId(rentalId);
        if (byRentalId.isPresent()) {
            Payment payment = byRentalId.get();
            if (payment.getStatus().name().equals("PAID")) {
                throw new RentalIsAlreadyPaid("Rental is already paid!");
            } else {
                return paymentMapper.toDto(payment);
            }
        }

        return createPayment(requestDto, rental);
    }

    @Override
    public PaymentDto successful(String sessionId) {
        Payment payment = findBySessionId(sessionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        PaymentDto paymentDto = paymentMapper.toDto(paymentRepository.save(payment));
        notificationService.onSuccessfulPayment(paymentDto, payment.getRental().getUser().getId());
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

    private PaymentDto createPayment(CreatePaymentRequestDto requestDto,
                                     RentalWithDetailedCarInfoDto rental) {
        Payment payment = paymentMapper.toModel(requestDto);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmountToPay(rentalService.getAmountToPay(rental.id()));
        try {
            Session session = stripeUtil.createStripeSession(
                    payment.getAmountToPay(), "Rental Payment"
            );
            payment.setSessionId(session.getId());
            payment.setSessionUrl(new URL(session.getUrl()));
            PaymentDto paymentDto = paymentMapper.toDto(paymentRepository.save(payment));
            notificationService.onPaymentCreationNotification(paymentDto, rental.userId());
            return paymentDto;
        } catch (StripeException | MalformedURLException e) {
            throw new PaymentException("Can't create payment session", e);
        }
    }
}
