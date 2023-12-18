package com.globalaccelerex.paymentapi.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String cardNumber;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;
    private String currency;
    private BigDecimal amount;
    private String cardHolderName;
    private String email;
    private String phoneNumber;
    private String transactionReference;
}

