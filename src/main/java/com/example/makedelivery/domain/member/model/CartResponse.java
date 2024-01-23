package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.Cart;
import com.example.makedelivery.domain.member.model.entity.CartOption;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
public class CartResponse {

    private Long id;
    private Integer price;
    private Long menuId;
    private String menuName;
    private Long storeId;
    private Integer count;

    @Builder.Default
    private List<CartOptionResponse> optionList = new ArrayList<>();

    public static CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .price(cart.getPrice())
                .menuId(cart.getMenuId())
                .menuName(cart.getMenuName())
                .storeId(cart.getStoreId())
                .count(cart.getCount())
                .build();
    }

    public void addOptionList(List<CartOption> optionList) {
        for ( CartOption option : optionList ) {
            CartOptionResponse cartOptionResponse = CartOptionResponse.toCartOptionResponse(option);
            this.optionList.add(cartOptionResponse);
        }
    }

}
