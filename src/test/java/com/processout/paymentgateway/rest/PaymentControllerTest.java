package com.processout.paymentgateway.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.processout.paymentgateway.rest.model.PaymentRequest;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("ec721f72-633e-4c73-ae14-3a757998291d")
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void badRequestResponseForMissingBodyValues() throws Exception {

        PaymentRequest paymentRequest = PaymentRequest.builder().build();
        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.card_number", Is.is("must not be blank")))
                .andExpect(jsonPath("$.card_cvc", Is.is("must not be blank")))
                .andExpect(jsonPath("$.card_expiry_year", Is.is("must not be blank")))
                .andExpect(jsonPath("$.currency", Is.is("must not be blank")))
                .andExpect(jsonPath("$.card_expiry_month", Is.is("must not be blank")))
                .andExpect(jsonPath("$.amount", Is.is("must not be null")))
                .andReturn();
    }

    @Test
    public void createAndRetrievePayment() throws Exception {

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(1)
                .card_cvc("123")
                .card_expiry_month("01")
                .card_expiry_year("23")
                .card_number("1234567887654321")
                .currency("EUR")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andDo( result -> {
                    JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
                    String paymentId = jsonNode.get("id").asText();
                    mockMvc.perform(get("/payment/{id}", paymentId))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.card_last_4", Is.is("4321")))
                            .andExpect(jsonPath("$.merchant_id", Is.is("ec721f72-633e-4c73-ae14-3a757998291d")))
                            .andExpect(jsonPath("$.currency", Is.is("EUR")))
                            .andExpect(jsonPath("$.amount", Is.is(1)))
                            ;
                });
    }

    @Test
    public void notFoundWhenPaymentDoesNotExist() throws Exception {
        mockMvc.perform(get("/payment/{id}", "b9d195e1-944f-493c-96f2-688a47d910ef"))
                .andExpect(status().isNotFound());
    }

}
