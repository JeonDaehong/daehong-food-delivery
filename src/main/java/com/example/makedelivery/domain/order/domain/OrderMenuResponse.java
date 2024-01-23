package com.example.makedelivery.domain.order.domain;

import com.example.makedelivery.domain.order.domain.entity.OrderMenu;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class OrderMenuResponse {

    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer menuCount;

    @Builder.Default
    private List<OrderMenuOptionResponse> menuOptionList = new ArrayList<>();

    public static OrderMenuResponse toOrderMenuResponse(OrderMenu orderMenu, String menuName, List<OrderMenuOptionResponse> menuOptionList) {
        return OrderMenuResponse.builder()
                .menuId(orderMenu.getMenuId())
                .menuName(menuName)
                .menuPrice(orderMenu.getPrice())
                .menuCount(orderMenu.getCount())
                .menuOptionList(menuOptionList)
                .build();
    }

}
