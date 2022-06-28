package com.processout.paymentgateway.external.service.acquiringbank.model.request;

import com.processout.paymentgateway.enums.Currency;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class AcquiringBankPaymentRequest {

    private final UUID merchantId;

    private final String cardNumber;

    private final Integer cardExpiryMonth;

    private final Integer cardExpiryYear;

    private final String cardCvc;

    private final Currency currency;

    /**
     * Amount to charge in the smallest unit of the currency, eg. for EUR123.45 enter 12345
     */
    private final Integer amount;
}
