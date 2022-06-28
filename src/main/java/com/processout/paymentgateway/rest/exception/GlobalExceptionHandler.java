package com.processout.paymentgateway.rest.exception;

import com.processout.paymentgateway.service.exception.AcquiringBankException;
import com.processout.paymentgateway.service.exception.PaymentNotFoundException;
import com.processout.paymentgateway.service.exception.UnsupportedCurrencyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedCurrencyException.class)
    public Map<String, String> handleUnsupportedCurrencyExceptions(UnsupportedCurrencyException ex) {
        return Map.of("currency", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PaymentNotFoundException.class)
    public Map<String, String> handlePaymentNotFoundExceptions() {
        return Map.of("error", "Payment not found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AcquiringBankException.class)
    public Map<String, String> handleAcquiringBankExceptions(AcquiringBankException e) {
        return Map.of("error", "Acquiring bank failed to process payment: " + e.getMessage());
    }


}
