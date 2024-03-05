package com.example.carsharing.util;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeUtil {
    private static final String DEFAULT_CURRENCY = "usd";
    @Value("${STRIPE_SUCCESS_LINK}")
    private String successUrl;
    @Value("${STRIPE_CANCEL_LINK}")
    private String cancelUrl;

    public StripeUtil(@Value("${STRIPE_API_KEY}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public Session createStripeSession(BigDecimal amount, String name) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(DEFAULT_CURRENCY)
                                        .setUnitAmountDecimal(convertToCents(amount))
                                        .setProductData(SessionCreateParams.LineItem
                                                .PriceData.ProductData.builder()
                                                .setName(name)
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                )
                .build();
        return Session.create(params);
    }

    private BigDecimal convertToCents(BigDecimal amount) {
        return amount.multiply(new BigDecimal(100));
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
