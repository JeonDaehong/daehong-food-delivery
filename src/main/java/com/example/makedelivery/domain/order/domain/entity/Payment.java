package com.example.makedelivery.domain.order.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Table(name = "TB_PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "PAYMENT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "PAYMENT_STATUS")
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Getter
    @RequiredArgsConstructor
    public enum PaymentType {
        CARD("카드 결제"),
        KAKAO_PAY("카카오페이 결제"),
        TOSS("토스 결제");

        private final String description;

    }

    @Getter
    @RequiredArgsConstructor
    public enum PaymentStatus {
        CANCEL_PAYMENT("결제 취소"),
        COMPLETE_PAYMENT("결제 완료");

        private final String description;

    }

    @Builder
    public Payment(Integer price, Long orderId, Long memberId,
                   PaymentType paymentType, PaymentStatus paymentStatus) {
        this.price = price;
        this.orderId = orderId;
        this.memberId = memberId;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
    }

    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCEL_PAYMENT;
    }

}
