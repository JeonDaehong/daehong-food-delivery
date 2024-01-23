package com.example.makedelivery.domain.order.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentStatus;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentType;
import com.example.makedelivery.domain.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.makedelivery.common.exception.ExceptionEnum.PAYMENT_INFO_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void payment(long orderId, long memberId, int price) {
        Payment payment = Payment.builder()
                .price(price)
                .orderId(orderId)
                .memberId(memberId)
                .paymentType(PaymentType.TOSS) // Toss
                .paymentStatus(PaymentStatus.COMPLETE_PAYMENT)
                .build();
        paymentRepository.save(payment);
    }

}
