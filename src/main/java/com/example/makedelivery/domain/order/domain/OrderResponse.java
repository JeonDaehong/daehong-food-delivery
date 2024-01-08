package com.example.makedelivery.domain.order.domain;

import com.example.makedelivery.domain.menu.domain.MenuGroupResponse;
import com.example.makedelivery.domain.menu.domain.MenuResponse;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.order.domain.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private String orderStatus;
    private Long deliveryAddressId;
    private String deliveryAddress;
    private Double deliveryLongitude;
    private Double deliveryLatitude;
    private Long storeId;
    private String storeName;
    private Integer originalPrice;
    private Integer discount;
    private Integer actualPrice;
    private List<OrderMenuResponse> orderMenuList; // 주문한 메뉴 리스트

    public static OrderResponse toOrderResponse(Order order, String deliveryAddress,
                                                Double deliveryLongitude, Double deliveryLatitude,
                                                String storeName,
                                                List<OrderMenuResponse> orderMenuResponseList) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus().toString())
                .deliveryAddressId(order.getAddressId())
                .deliveryAddress(deliveryAddress)
                .deliveryLongitude(deliveryLongitude)
                .deliveryLatitude(deliveryLatitude)
                .storeId(order.getStoreId())
                .storeName(storeName)
                .originalPrice(order.getOriginalPrice())
                .discount(order.getDiscount())
                .actualPrice(order.getActualPrice())
                .orderMenuList(orderMenuResponseList)
                .build();
    }

}
