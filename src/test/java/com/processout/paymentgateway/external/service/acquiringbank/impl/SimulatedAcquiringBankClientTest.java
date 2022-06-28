package com.processout.paymentgateway.external.service.acquiringbank.impl;

import com.processout.paymentgateway.external.service.acquiringbank.model.request.AcquiringBankPaymentRequest;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SimulatedAcquiringBankClientTest {

    @InjectMocks
    private SimulatedAcquiringBankClient simulatedAcquiringBankClient;

    @Test
    void currencySupported() {
        assertTrue(simulatedAcquiringBankClient.isCurrencySupported("EUR"));
    }

    @Test
    void currencyUnsupported() {
        assertFalse(simulatedAcquiringBankClient.isCurrencySupported("XXX"));
    }

    @Test
    void processPaymentSuccessful() {
        AcquiringBankPaymentRequest request = AcquiringBankPaymentRequest.builder().cardCvc("123").build();
        assertTrue(simulatedAcquiringBankClient.processPayment(request).getIsSuccessful());
    }

    @Test
    void processPaymentUnuccessful() {
        AcquiringBankPaymentRequest request = AcquiringBankPaymentRequest.builder().cardCvc("000").build();
        PaymentResponse response = simulatedAcquiringBankClient.processPayment(request);
        assertFalse(response.getIsSuccessful());
        assertEquals("Card declined", response.getErrorMessage());
    }
}
