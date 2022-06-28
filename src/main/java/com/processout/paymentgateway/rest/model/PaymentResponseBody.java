package com.processout.paymentgateway.rest.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class PaymentResponseBody {
    private UUID id;
}
