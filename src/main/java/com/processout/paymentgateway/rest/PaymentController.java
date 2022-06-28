package com.processout.paymentgateway.rest;

import com.processout.paymentgateway.persistence.entity.Payment;
import com.processout.paymentgateway.rest.model.PaymentRequest;
import com.processout.paymentgateway.rest.model.PaymentResponseBody;
import com.processout.paymentgateway.service.PaymentGatewayService;
import com.processout.paymentgateway.service.exception.AcquiringBankException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Payments", description = "Process and fetch payments")
@RestController
public class PaymentController {

    final private PaymentGatewayService paymentGatewayService;

    public PaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @Operation(summary = "Process a payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error calling acquiring bank", content = @Content)
    })
    @PostMapping(value = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponseBody> processPayment(@Valid @RequestBody PaymentRequest paymentRequest,
                                                              Authentication authentication) throws AcquiringBankException {
        UUID paymentId = paymentGatewayService.processPayment(paymentRequest);

        PaymentResponseBody paymentResponseBody = PaymentResponseBody.builder().id(paymentId).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseBody);
    }

    @Operation(summary = "Fetch a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Payment.class))}),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content)})
    @GetMapping(value = "/payment/{id}")
    public Payment findPaymentById(@PathVariable("id") UUID id) {
        return paymentGatewayService.fetchPayment(id);
    }
}
