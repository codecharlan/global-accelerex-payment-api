package com.globalaccelerex.paymentapi;

import com.flutterwave.services.CardCharge;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentApiApplication {
    @Bean
    public CardCharge cardCharge() {
        return new CardCharge();
    }

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }

}
