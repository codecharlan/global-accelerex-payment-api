package com.globalaccelerex.paymentapi.repository;

import com.globalaccelerex.paymentapi.enums.DeviceType;
import com.globalaccelerex.paymentapi.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findPaymentsByDeviceType(DeviceType deviceType);
    Page<Payment> findPaymentsByDeviceType(DeviceType deviceType, Pageable pageable);

}
