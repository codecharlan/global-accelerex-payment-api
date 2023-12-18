package com.globalaccelerex.paymentapi.controller;

import com.globalaccelerex.paymentapi.dtos.request.PaymentRequest;
import com.globalaccelerex.paymentapi.dtos.response.ApiResponse;
import com.globalaccelerex.paymentapi.dtos.response.PaymentResponseDTO;
import com.globalaccelerex.paymentapi.enums.DeviceType;
import com.globalaccelerex.paymentapi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> makePayment(@RequestHeader("User-Agent") String userAgent, @RequestBody PaymentRequest paymentRequest) throws IOException {
        ApiResponse<PaymentResponseDTO> response = paymentService.makePayment(paymentRequest, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPayments(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) DeviceType deviceType
    ) {
        ApiResponse<List<PaymentResponseDTO>> payments = paymentService.getPayments(offset, limit, deviceType);
        return ResponseEntity.ok(payments);
    }
}
