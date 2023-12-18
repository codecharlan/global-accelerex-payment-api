package com.globalaccelerex.paymentapi.service.serviceImpl;

import com.globalaccelerex.paymentapi.dtos.response.ApiResponse;
import com.globalaccelerex.paymentapi.dtos.response.PaymentResponseDTO;
import com.globalaccelerex.paymentapi.enums.DeviceType;
import com.globalaccelerex.paymentapi.entity.Payment;
import com.globalaccelerex.paymentapi.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardPaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private CardPaymentService cardPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPayments_PaginatedWithDeviceFilter_FetchesCorrectData() {
        List<Payment> samplePayments = generateSamplePayments();

        when(paymentRepository.findPaymentsByDeviceType(eq(DeviceType.MOBILE), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    int pageSize = pageable.getPageSize();
                    int currentPage = pageable.getPageNumber();
                    int start = currentPage * pageSize;
                    int end = Math.min(start + pageSize, samplePayments.size());

                    return new PageImpl<>(samplePayments.subList(start, end), pageable, samplePayments.size());
                });

        ApiResponse<List<PaymentResponseDTO>> response = cardPaymentService.getPayments(0, 5, DeviceType.MOBILE);

        assertThat(response.getData()).hasSize(5);
        for (PaymentResponseDTO dto : response.getData()) {
            assertThat(dto.getDeviceType()).isEqualTo(DeviceType.MOBILE);
        }
    }
    private List<Payment> generateSamplePayments() {
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Payment payment = new Payment();
            payment.setDeviceType(DeviceType.MOBILE);
            payments.add(payment);
        }
        return payments;
    }
}
