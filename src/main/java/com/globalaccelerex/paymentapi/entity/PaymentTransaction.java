package com.globalaccelerex.paymentapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.globalaccelerex.paymentapi.enums.TransactionStatus;
import com.globalaccelerex.paymentapi.model.CustomerInfo;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class PaymentTransaction {

    @Id
    private Long id;

    @Column(name = "tx_ref", unique = true)
    private String txRef;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentid")
    @JsonIgnore
    private Payment payment;

    @Embedded
    @JsonIgnore
    private CustomerInfo customer;
}
