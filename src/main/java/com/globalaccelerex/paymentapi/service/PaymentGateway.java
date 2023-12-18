package com.globalaccelerex.paymentapi.service;

import com.globalaccelerex.paymentapi.dtos.request.PaymentRequest;
import com.globalaccelerex.paymentapi.dtos.response.ApiResponse;
import com.globalaccelerex.paymentapi.dtos.response.PaymentResponseDTO;
import com.globalaccelerex.paymentapi.enums.DeviceType;

import java.io.IOException;

public interface PaymentGateway {
    ApiResponse<PaymentResponseDTO> processPayment(PaymentRequest paymentRequest, DeviceType deviceType) throws IOException;
}