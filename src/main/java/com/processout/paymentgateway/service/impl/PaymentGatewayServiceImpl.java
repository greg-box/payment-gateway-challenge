package com.processout.paymentgateway.service.impl;

import com.processout.paymentgateway.enums.Currency;
import com.processout.paymentgateway.external.service.acquiringbank.AcquiringBankClient;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.AcquiringBankPaymentRequest;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.PaymentResponse;
import com.processout.paymentgateway.persistence.entity.Payment;
import com.processout.paymentgateway.persistence.repository.PaymentRepository;
import com.processout.paymentgateway.service.exception.AcquiringBankException;
import com.processout.paymentgateway.service.exception.PaymentNotFoundException;
import com.processout.paymentgateway.service.exception.UnsupportedCurrencyException;
import com.processout.paymentgateway.rest.model.PaymentRequest;
import com.processout.paymentgateway.service.AuthenticationFacade;
import com.processout.paymentgateway.service.PaymentGatewayService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    final private AuthenticationFacade authenticationFacade;

    final private PaymentRepository paymentRepository;

    final private List<AcquiringBankClient> acquiringBankClients;

    public PaymentGatewayServiceImpl(AuthenticationFacade authenticationFacade, PaymentRepository paymentRepository,
                                     List<AcquiringBankClient> acquiringBankClients) {
        this.authenticationFacade = authenticationFacade;
        this.paymentRepository = paymentRepository;
        this.acquiringBankClients = acquiringBankClients;
    }

    @Override
    public UUID processPayment(PaymentRequest paymentRequest) throws AcquiringBankException {
        // get merchant id from spring security
        UUID merchantId = UUID.fromString(authenticationFacade.getAuthentication().getName());

        // find the first acquiring bank that supports the requested currency
        AcquiringBankClient acquiringBankClient = acquiringBankClients.stream().filter(client ->
                        client.isCurrencySupported(paymentRequest.getCurrency()))
                .findFirst().orElseThrow(
                        () -> new UnsupportedCurrencyException("Unsupported currency: " + paymentRequest.getCurrency()));

        // call the acquiring bank
        PaymentResponse response = acquiringBankClient.processPayment(
                buildAcquiringBankPaymentRequest(paymentRequest, merchantId));
        if (!response.getIsSuccessful()) {
            throw new AcquiringBankException(response.getErrorMessage());
        }

        // store the details in the db
        Payment payment = buildPayment(paymentRequest, merchantId);
        paymentRepository.save(payment);

        return payment.getId();
    }

    private AcquiringBankPaymentRequest buildAcquiringBankPaymentRequest(PaymentRequest paymentRequest, UUID merchantId) {
        return AcquiringBankPaymentRequest.builder()
                .cardNumber(paymentRequest.getCard_number())
                .cardCvc(paymentRequest.getCard_cvc())
                .amount(paymentRequest.getAmount())
                .cardExpiryMonth(Integer.valueOf(paymentRequest.getCard_expiry_month()))
                .cardExpiryYear(Integer.valueOf(paymentRequest.getCard_expiry_year()))
                .currency(Currency.valueOf(paymentRequest.getCurrency()))
                .merchantId(merchantId)
                .build();
    }

    private Payment buildPayment(PaymentRequest paymentRequest, UUID merchantId) {
        String cardNumber = paymentRequest.getCard_number();
        return Payment.builder()
                .amount(paymentRequest.getAmount())
                .card_last_4(cardNumber.substring(cardNumber.length() - 4))
                .currency(paymentRequest.getCurrency())
                .merchant_id(merchantId)
                .build();
    }

    @Override
    public Payment fetchPayment(UUID id) {
        Optional<Payment> payment = paymentRepository
                .findByPaymentIdAndMerchantId(id, UUID.fromString(authenticationFacade.getAuthentication().getName()));

        return payment.orElseThrow(PaymentNotFoundException::new);
    }
}
