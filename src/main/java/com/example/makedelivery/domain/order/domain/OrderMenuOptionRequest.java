package com.example.makedelivery.domain.order.domain;

import com.example.makedelivery.domain.order.domain.entity.OrderMenuOption;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderMenuOptionRequest {

    @NotEmpty(message = "가격은 공란일 수 없습니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull
    private Long menuOptionId;

    public static OrderMenuOption toEntity(OrderMenuOptionRequest request, Long orderMenuId) {
        return OrderMenuOption.builder()
                .orderMenuId(orderMenuId)
                .optionId(request.getMenuOptionId())
                .price(request.getPrice())
                .build();
    }

}
