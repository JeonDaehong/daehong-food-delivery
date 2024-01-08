package com.example.makedelivery.domain.order.domain;

import com.example.makedelivery.domain.order.domain.entity.OrderMenuOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderMenuOptionResponse {

    private Long optionId;
    private String optionName;
    private Integer optionPrice;

    public static OrderMenuOptionResponse toOrderMenuOptionResponse(OrderMenuOption orderMenuOption, String optionName) {
        return OrderMenuOptionResponse.builder()
                .optionId(orderMenuOption.getOptionId())
                .optionName(optionName)
                .optionPrice(orderMenuOption.getPrice())
                .build();
    }

}
