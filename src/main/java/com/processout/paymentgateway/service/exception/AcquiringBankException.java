package com.processout.paymentgateway.service.exception;

public class AcquiringBankException extends Throwable {
    public AcquiringBankException(String errorMessage) {
        super(errorMessage);
    }
}
