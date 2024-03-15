package com.example.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.exception.RentalIsAlreadyPaid;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.Payment;
import com.example.carsharing.repository.PaymentRepository;
import com.example.carsharing.service.impl.StripePaymentServiceImpl;
import com.example.carsharing.supplier.PaymentSupplier;
import com.example.carsharing.supplier.RentalSupplier;
import com.example.carsharing.util.StripeUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StripePaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private RentalService rentalService;
    @Mock
    private StripeUtil stripeUtil;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private StripePaymentServiceImpl paymentService;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateRequestDto_ReturnsPaymentDto()
            throws MalformedURLException, StripeException {
        CreatePaymentRequestDto requestDto =
                PaymentSupplier.getPaymentCreatePaymentRequestDto();
        RentalWithDetailedCarInfoDto rental =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();
        Payment payment = PaymentSupplier.getPayment();
        Session session = PaymentSupplier.getSession();
        PaymentDto expected = PaymentSupplier.getPaymentDto();

        when(rentalService.findById(rental.id())).thenReturn(rental);
        when(paymentRepository.findByRentalId(rental.id())).thenReturn(Optional.empty());
        when(paymentMapper.toModel(requestDto)).thenReturn(payment);
        when(rentalService.getAmountToPay(rental.id()))
                .thenReturn(payment.getAmountToPay());
        when(stripeUtil.createStripeSession(payment.getAmountToPay(), "Rental Payment"))
                .thenReturn(session);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        PaymentDto actual = paymentService.save(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalService, times(1)).findById(rental.id());
        verify(paymentRepository, times(1)).findByRentalId(rental.id());
        verify(paymentMapper, times(1)).toModel(requestDto);
        verify(rentalService, times(1)).getAmountToPay(rental.id());
        verify(stripeUtil, times(1))
                .createStripeSession(payment.getAmountToPay(), "Rental Payment");
        verify(paymentRepository, times(1)).save(payment);
        verify(paymentMapper, times(1)).toDto(payment);
        verify(notificationService, times(1))
                .onPaymentCreationNotification(expected, rental.userId());
        verifyNoMoreInteractions(rentalService, paymentRepository,
                paymentMapper, stripeUtil, notificationService);
    }

    @Test
    @DisplayName("Verify save() method works when payment is already created")
    public void save_PaymentAlreadyCreated_ReturnsPaymentDto() throws MalformedURLException {
        CreatePaymentRequestDto requestDto =
                PaymentSupplier.getPaymentCreatePaymentRequestDto();
        RentalWithDetailedCarInfoDto rental =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();
        Payment payment = PaymentSupplier.getPayment();
        PaymentDto expected = PaymentSupplier.getPaymentDto();

        when(rentalService.findById(rental.id())).thenReturn(rental);
        when(paymentRepository.findByRentalId(rental.id())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        PaymentDto actual = paymentService.save(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalService, times(1)).findById(rental.id());
        verify(paymentRepository, times(1)).findByRentalId(rental.id());
        verify(paymentMapper, times(1)).toDto(payment);
        verifyNoMoreInteractions(rentalService, paymentRepository,
                paymentMapper, stripeUtil, notificationService);
    }

    @Test
    @DisplayName("Verify save() method throws exception when payment paid")
    public void save_PaymentAlreadyPaid_ThrowsException() throws MalformedURLException {
        RentalWithDetailedCarInfoDto rental =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();
        Payment payment = PaymentSupplier.getPayment();
        payment.setStatus(Payment.PaymentStatus.PAID);
        CreatePaymentRequestDto requestDto =
                PaymentSupplier.getPaymentCreatePaymentRequestDto();

        when(rentalService.findById(rental.id())).thenReturn(rental);
        when(paymentRepository.findByRentalId(rental.id())).thenReturn(Optional.of(payment));

        RentalIsAlreadyPaid exception = assertThrows(
                RentalIsAlreadyPaid.class,
                () -> paymentService.save(requestDto)
        );

        String expected = "Rental is already paid!";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(rentalService, times(1)).findById(rental.id());
        verify(paymentRepository, times(1)).findByRentalId(rental.id());
        verifyNoMoreInteractions(rentalService, paymentRepository,
                paymentMapper, stripeUtil, notificationService);
    }
}
