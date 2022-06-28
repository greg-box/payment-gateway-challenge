package com.processout.paymentgateway.service.impl;

import com.processout.paymentgateway.external.service.acquiringbank.AcquiringBankClient;
import com.processout.paymentgateway.external.service.acquiringbank.model.request.PaymentResponse;
import com.processout.paymentgateway.persistence.repository.PaymentRepository;
import com.processout.paymentgateway.service.exception.AcquiringBankException;
import com.processout.paymentgateway.service.exception.UnsupportedCurrencyException;
import com.processout.paymentgateway.rest.model.PaymentRequest;
import com.processout.paymentgateway.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceImplTest {

    private static final String MERCHANT_ID = "8569e441-70e2-4079-b500-23b3cee9c248";

    @Mock
    private AcquiringBankClient acquiringBankClient;

    @Mock
    private List<AcquiringBankClient> acquiringBankClients;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private Authentication authentication;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentGatewayServiceImpl paymentGatewayService;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn(MERCHANT_ID);
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void unsupportedCurrency() {
        when(acquiringBankClient.isCurrencySupported(any())).thenReturn(false);
        when(acquiringBankClients.stream()).thenReturn(Stream.of(acquiringBankClient));

        PaymentRequest paymentRequest = PaymentRequest.builder().currency("XXX").build();

        UnsupportedCurrencyException e = assertThrows(UnsupportedCurrencyException.class,
                () -> paymentGatewayService.processPayment(paymentRequest));
        assertThat(e.getMessage()).isEqualTo("Unsupported currency: XXX");
    }

    @Test
    void acquiringBankError() {
        when(acquiringBankClient.isCurrencySupported(any())).thenReturn(true);
        when(acquiringBankClient.processPayment(any()))
                .thenReturn(PaymentResponse.builder()
                        .isSuccessful(false).errorMessage("Card declined").build());
        when(acquiringBankClients.stream()).thenReturn(Stream.of(acquiringBankClient));

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .card_number("1234567887654321")
                .card_expiry_month("01")
                .card_expiry_year("23")
                .currency("EUR")
                .build();

        AcquiringBankException e = assertThrows(AcquiringBankException.class,
                () -> paymentGatewayService.processPayment(paymentRequest));
        assertThat(e.getMessage()).isEqualTo("Card declined");
    }
}
