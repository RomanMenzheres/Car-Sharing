package com.example.carsharing.supplier;

import com.example.carsharing.dto.payment.CreatePaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.model.Payment;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PaymentSupplier {

    public static CreatePaymentRequestDto getFineCreatePaymentRequestDto() {
        return new CreatePaymentRequestDto(
                4L,
                "FINE"
        );
    }

    public static CreatePaymentRequestDto getPaymentCreatePaymentRequestDto() {
        return new CreatePaymentRequestDto(
                4L,
                "PAYMENT"
        );
    }

    public static Payment getPayment() throws MalformedURLException {
        Payment payment = new Payment();
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setType(Payment.PaymentType.PAYMENT);
        payment.setAmountToPay(BigDecimal.valueOf(1400));
        payment.setRental(RentalSupplier.getNotActiveRental());
        payment.setSessionUrl(new URL("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"));
        payment.setSessionId("cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7");
        return payment;
    }

    public static PaymentDto getPaymentDto() throws MalformedURLException {
        return new PaymentDto(
                1L,
                4L,
                "PENDING",
                "PAYMENT",
                new URL("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"),
                "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7",
                BigDecimal.valueOf(1400)
        );
    }

    public static CreatePaymentRequestDto getInvalidCreatePaymentRequestDto() {
        return new CreatePaymentRequestDto(
                -1L,
                "GIVE_MY_MONEY_BACK!"
        );
    }

    public static CreatePaymentRequestDto getCreatePaymentRequestDtoWithPaidRental() {
        return new CreatePaymentRequestDto(
                3L,
                "PAYMENT"
        );
    }

    public static List<PaymentDto> getPaymentsByUserWithId1() throws MalformedURLException {
        return List.of(
                new PaymentDto(
                        3L,
                        2L,
                        "PENDING",
                        "PAYMENT",
                        new URL("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"),
                        "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7",
                        BigDecimal.valueOf(1050)
                ),

                new PaymentDto(
                        4L,
                        3L,
                        "PAID",
                        "PAYMENT",
                        new URL("https://checkout.stripe.com/c/pay/cs_test_a14M6NVrnar1kMtZ0PxkJysMgTkfzrObNJosSl4PUEWOMKkWxdZotxt8Hj#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"),
                        "cs_test_a14M6NVrnar1kMtZ0PxkJysMgTkfzrObNJosSl4PUEWOMKkWxdZotxt8Hj",
                        BigDecimal.valueOf(750)
                )
        );
    }

    public static PaymentDto getSuccessPaymentDto() throws MalformedURLException {
        return new PaymentDto(
                3L,
                2L,
                "PAID",
                "PAYMENT",
                new URL("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"),
                "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7",
                BigDecimal.valueOf(1050)
        );
    }

    public static PaymentDto getCanceledPaymentDto() throws MalformedURLException {
        return new PaymentDto(
                3L,
                2L,
                "CANCELED",
                "PAYMENT",
                new URL("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"),
                "cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7",
                BigDecimal.valueOf(1050)
        );
    }

    public static Session getSession() {
        Session session = new Session();
        session.setId("cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1OOcLaCUOFF9gl6xPr0YEzC5hbRt9hHBZuJc6AnMB2oO6WzEXK9SAHUd7#fidkdWxOYHwnPyd1blpxYHZxWjA0SnRHVWpJY3Myb19XPG5Sb3w2UVRAY29yfW1dMXR8ZDVuSV1HZEJnTWpvf3NnaH9LN11rTFB8TENxdzxVal01QmZAf1FqdGo8SW9nNHJrZjVcXGB1bm9BNTVNZzEzVW5gSycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl");
        return session;
    }
}
