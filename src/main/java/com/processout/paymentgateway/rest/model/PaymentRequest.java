package com.processout.paymentgateway.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
public class PaymentRequest {

    @NotBlank
    @Size(min = 16, max = 16)
    @Pattern(regexp = "[0-9]+")
    @Schema(example = "1234567812345678")
    private String card_number;

    @NotBlank
    @Size(min = 3, max = 3)
    @Pattern(regexp = "[0-9]+")
    @Schema(example = "123")
    private String card_cvc;

    @NotBlank
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[0-9]+")
    @Schema(example = "12")
    private String card_expiry_month;

    @NotBlank
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[0-9]+")
    @Schema(example = "23")
    private String card_expiry_year;

    @NotNull
    @Min(1)
    @Schema(description = "The payment in the smallest unit of the currency eg. 12345 for $123.45", example = "12345")
    private Integer amount;

    @NotBlank
    @Size(min = 3, max = 3)
    @Pattern(regexp = "[A-Z]+")
    @Schema(example = "EUR")
    private String currency;
}
