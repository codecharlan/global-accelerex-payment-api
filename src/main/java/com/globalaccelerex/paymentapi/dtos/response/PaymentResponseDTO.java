package com.globalaccelerex.paymentapi.dtos.response;

import com.globalaccelerex.paymentapi.enums.DeviceType;
import com.globalaccelerex.paymentapi.enums.PaymentMethod;
import com.globalaccelerex.paymentapi.model.CustomerInfo;
import com.globalaccelerex.paymentapi.entity.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private CustomerInfo customer;
    private DeviceType deviceType;
    private List<PaymentTransaction> transactions;
}