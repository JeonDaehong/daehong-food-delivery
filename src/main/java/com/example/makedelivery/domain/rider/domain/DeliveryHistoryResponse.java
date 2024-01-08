package com.example.makedelivery.domain.rider.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryHistoryResponse {

    private Long deliveryId;
    private Long orderId;
    private String address;

    public static DeliveryHistoryResponse toDeliveryHistoryResponse(Long deliveryId, Long orderId, String address) {
        return DeliveryHistoryResponse.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .address(address)
                .build();
    }

}
