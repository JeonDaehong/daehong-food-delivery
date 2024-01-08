package com.example.makedelivery.domain.order.service;

import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentStatus;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentType;
import com.example.makedelivery.domain.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public void payment(long orderId, long memberId, int price) {
        Payment payment = Payment.builder()
                .price(price)
                .orderId(orderId)
                .memberId(memberId)
                .paymentType(PaymentType.CARD) // Card
                .paymentStatus(PaymentStatus.COMPLETE_PAYMENT)
                .build();
        paymentRepository.save(payment);
    }

}
