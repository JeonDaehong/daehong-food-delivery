package com.example.makedelivery.domain.order.domain;

import com.example.makedelivery.domain.order.domain.entity.OrderMenu;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderMenuRequest {

    @NotNull
    private Long cartId;

    @NotNull
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull
    private Long menuId;

    @NotEmpty(message = "개수는 공란일 수 없습니다.")
    @Min(value = 1, message = "개수는 1개 이상이어야 합니다.")
    private Integer count;

    private List<OrderMenuOptionRequest> optionList;

    public static OrderMenu toEntity(OrderMenuRequest request, Long orderId) {
        return OrderMenu.builder()
                .orderId(orderId)
                .menuId(request.getMenuId())
                .price(request.getPrice())
                .count(request.getCount())
                .build();
    }

}
