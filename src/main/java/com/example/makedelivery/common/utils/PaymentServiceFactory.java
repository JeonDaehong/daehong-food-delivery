package com.example.makedelivery.common.utils;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentType;
import com.example.makedelivery.domain.order.service.CardPaymentService;
import com.example.makedelivery.domain.order.service.KakaoPaymentService;
import com.example.makedelivery.domain.order.service.PaymentService;
import com.example.makedelivery.domain.order.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Component
@RequiredArgsConstructor
public class PaymentServiceFactory {

    private final CardPaymentService cardPaymentService;
    private final KakaoPaymentService kakaoPaymentService;
    private final TossPaymentService tossPaymentService;

    public PaymentService getPaymentService(PaymentType type) {
        PaymentService paymentService;

        switch (type) {
            case CARD -> paymentService = cardPaymentService; // 카드 결제
            case KAKAO_PAY -> paymentService = kakaoPaymentService; // 카카오페이
            case TOSS -> paymentService = tossPaymentService; // 토스 결제
            default -> throw new ApiException(PAYMENT_NOT_FOUND); // 지원하지 않는 결제 서비스
        }

        return paymentService;
    }

}
