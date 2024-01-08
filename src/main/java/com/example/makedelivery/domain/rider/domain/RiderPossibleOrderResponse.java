package com.example.makedelivery.domain.rider.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RiderPossibleOrderResponse {

    private Long orderId;
    private Long storeId;
    private String deliveryAddress;
    private String storeName;

    public static RiderPossibleOrderResponse toRiderPossibleOrderResponse(Long orderId, Long storeId, String storeName, String deliveryAddress) {
        return RiderPossibleOrderResponse.builder()
                .orderId(orderId)
                .storeId(storeId)
                .storeName(storeName)
                .deliveryAddress(deliveryAddress)
                .build();
    }

}
