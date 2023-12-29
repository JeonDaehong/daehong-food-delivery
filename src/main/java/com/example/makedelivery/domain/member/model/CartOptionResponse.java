package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.Cart;
import com.example.makedelivery.domain.member.model.entity.CartOption;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartOptionResponse {

    private Long id;
    private Integer price;
    private Long menuOptionId;
    private String menuOptionName;

    public static CartOptionResponse toCartOptionResponse(CartOption cartOption) {
        return CartOptionResponse.builder()
                .id(cartOption.getId())
                .price(cartOption.getPrice())
                .menuOptionId(cartOption.getMenuOptionId())
                .menuOptionName(cartOption.getMenuOptionName())
                .build();
    }

}
