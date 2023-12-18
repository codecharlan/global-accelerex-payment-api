package com.globalaccelerex.paymentapi.repository;

import com.globalaccelerex.paymentapi.entity.Payment;
import com.globalaccelerex.paymentapi.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findAllByPayment(Payment payment);
//    Boolean TxRefExists();
}
