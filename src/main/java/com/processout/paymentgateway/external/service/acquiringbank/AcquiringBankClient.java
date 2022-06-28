package com.processout.paymentgateway.external.service.acquiringbank;

import com.processout.paymentgateway.external.service.acquiringbank.model.request.AcquiringBankPaymentRequest;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.PaymentResponse;

public interface AcquiringBankClient {

    Boolean isCurrencySupported(String currency);

    PaymentResponse processPayment(AcquiringBankPaymentRequest acquiringBankPaymentRequest);
}
