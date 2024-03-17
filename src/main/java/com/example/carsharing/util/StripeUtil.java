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
    public static final BigDecimal CENT_MULTIPLIER = new BigDecimal(100);
    public static final Long DEFAULT_QUANTITY = 1L;
    private static final String DEFAULT_CURRENCY = "usd";
    @Value("${stripe.success.link}")
    private String successUrl;
    @Value("${stripe.cancel.link}")
    private String cancelUrl;

    public StripeUtil(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public Session createStripeSession(BigDecimal amount, String name) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
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
        return amount.multiply(CENT_MULTIPLIER);
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
