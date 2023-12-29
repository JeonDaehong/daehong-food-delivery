package com.example.makedelivery.domain.member.model;


import com.example.makedelivery.domain.member.model.entity.Cart;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class CartRequest {

    @NotEmpty(message = "가격은 공란일 수 없습니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull
    private Long menuId;

    @NotNull
    private String menuName;

    @NotNull
    private Long storeId;

    @NotEmpty(message = "개수는 공란일 수 없습니다.")
    @Min(value = 1, message = "개수는 1개 이상이어야 합니다.")
    private Integer count;

    List<CartOptionRequest> optionList = new ArrayList<>();

    public static Cart toEntity(CartRequest request, Long memberId) {
        return Cart.builder()
                .price(request.getPrice())
                .menuId(request.getMenuId())
                .menuName(request.getMenuName())
                .storeId(request.getStoreId())
                .memberId(memberId)
                .count(request.getCount())
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
