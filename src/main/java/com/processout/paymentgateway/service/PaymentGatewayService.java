package com.processout.paymentgateway.service;

import com.processout.paymentgateway.persistence.entity.Payment;
import com.processout.paymentgateway.rest.model.PaymentRequest;
import com.processout.paymentgateway.service.exception.AcquiringBankException;

import java.util.UUID;

public interface PaymentGatewayService {

    UUID processPayment(PaymentRequest paymentRequest) throws AcquiringBankException;

    Payment fetchPayment(UUID id);
}
