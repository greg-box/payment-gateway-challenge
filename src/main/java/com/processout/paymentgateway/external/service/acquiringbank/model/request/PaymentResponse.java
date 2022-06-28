package com.processout.paymentgateway.external.service.acquiringbank.model.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentResponse {

    private Boolean isSuccessful;

    private String errorMessage;
}
