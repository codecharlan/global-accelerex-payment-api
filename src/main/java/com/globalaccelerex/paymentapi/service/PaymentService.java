package com.globalaccelerex.paymentapi.service;

import com.globalaccelerex.paymentapi.dtos.request.PaymentRequest;
import com.globalaccelerex.paymentapi.dtos.response.ApiResponse;
import com.globalaccelerex.paymentapi.dtos.response.PaymentResponseDTO;
import com.globalaccelerex.paymentapi.enums.DeviceType;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

public abstract class PaymentService {
    @Transactional
    public abstract ApiResponse<PaymentResponseDTO> makePayment(PaymentRequest paymentRequest, String userAgent) throws IOException;
    public abstract ApiResponse<List<PaymentResponseDTO>> getPayments(Integer offset, Integer limit, DeviceType deviceType);
}
