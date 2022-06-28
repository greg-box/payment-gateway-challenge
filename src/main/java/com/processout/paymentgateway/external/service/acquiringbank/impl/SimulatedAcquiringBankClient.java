package com.processout.paymentgateway.external.service.acquiringbank.impl;

import com.processout.paymentgateway.enums.Currency;
import com.processout.paymentgateway.external.service.acquiringbank.AcquiringBankClient;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.AcquiringBankPaymentRequest;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SimulatedAcquiringBankClient implements AcquiringBankClient {

    @Override
    public Boolean isCurrencySupported(String currency) {
        List<String> supportedCurrencies = Arrays.asList(Currency.EUR.name(), Currency.GBP.name());
        return supportedCurrencies.contains(currency);
    }

    @Override
    public PaymentResponse processPayment(AcquiringBankPaymentRequest acquiringBankPaymentRequest) {

        // Validate card details

        // Check sufficient funds are available to make payment

        // Check merchant account is able to receive funds

        // Execute the payment

        PaymentResponse response = PaymentResponse.builder().isSuccessful(true).build();

        // To test failure scenario enter CVC of 000
        if (acquiringBankPaymentRequest.getCardCvc().equals("000")) {
            response = PaymentResponse.builder().isSuccessful(false).errorMessage("Card declined").build();
        }

        return response;
    }

}
