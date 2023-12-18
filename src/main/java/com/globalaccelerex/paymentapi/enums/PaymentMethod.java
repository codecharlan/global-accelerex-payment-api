package com.globalaccelerex.paymentapi.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
        CARD("Active"),
        MOBILE_MONEY("Upcoming"),
        BANK_TRANSFER("Upcoming"),
        WALLET("Upcoming");

    PaymentMethod(String status) {
    }

}