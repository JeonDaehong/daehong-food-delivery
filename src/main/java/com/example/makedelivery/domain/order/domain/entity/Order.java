package com.example.makedelivery.domain.order.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Table(name = "TB_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "ORDER_ADDRESS_ID")
    private Long addressId;

    @Column(name = "ORDER_MEMBER_ID")
    private Long memberId;

    @Column(name = "ORDER_STORE_ID")
    private Long storeId;

    @Column(name = "ORIGINAL_PRICE")
    private Integer originalPrice; //총 주문 금액

    @Column(name = "DISCOUNT")
    private Integer discount; //포인트, 쿠폰 등으로 할인 되는 금액

    @Column(name = "ACTUAL_PRICE")
    private Integer actualPrice; //실제 결제하는 금액

    @Column(name = "ORDER_STATUS")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "RIDER_ID")
    private Long riderId;

    @Version
    private Long version;

    @Getter
    @RequiredArgsConstructor
    public enum OrderStatus {
        CANCEL_ORDER("주문 취소"),
        COMPLETE_ORDER("주문 완료"),
        APPROVED_ORDER("주문 승인"),
        DELIVERY_WAIT("배송 대기 중"),
        DELIVERING("배송 중"),
        COMPLETE_DELIVERY("배송 완료");

        private final String description;

    }

    @Builder
    public Order(Long addressId, Long memberId, Long storeId, Integer originalPrice,
                 Integer discount, Integer actualPrice, OrderStatus orderStatus) {
        this.addressId = addressId;
        this.memberId = memberId;
        this.storeId = storeId;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.actualPrice = actualPrice;
        this.orderStatus = orderStatus;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL_ORDER;
    }

    public void changeOrderStatusApprove() {
        this.orderStatus = OrderStatus.APPROVED_ORDER;
    }

    public void changeOrderStatusDeliveryWait() {
        this.orderStatus = OrderStatus.DELIVERY_WAIT;
    }

    public void completedDelivery() { this.orderStatus = OrderStatus.COMPLETE_DELIVERY; }

    public void registerRider(Long riderId) {
        this.riderId = riderId;
        this.orderStatus = OrderStatus.DELIVERING;
    }

    public void cancelRider() {
        this.riderId = null;
        this.orderStatus = OrderStatus.DELIVERY_WAIT;
    }

}
