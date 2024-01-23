package com.example.makedelivery.domain.rider.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import com.example.makedelivery.domain.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Table(name = "TB_DELIV_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiderDeliveryHistory extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIV_ID")
    private Long id;

    @Column(name = "RIDER_ID")
    private Long riderId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "DELIV_ADDRESS")
    private String deliveryAddress;

    @Column(name = "DELIEV_STATUS")
    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Getter
    @RequiredArgsConstructor
    public enum DeliveryStatus {
        DELIVERING("배달 중"),
        COMPLETED("배달 완료");

        private final String description;
    }

    @Builder
    public RiderDeliveryHistory(Long riderId, Long orderId, String deliveryAddress, DeliveryStatus deliveryStatus) {
        this.riderId = riderId;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
    }

    public void completedDelivery() {
        this.deliveryStatus = DeliveryStatus.COMPLETED;
    }

}
