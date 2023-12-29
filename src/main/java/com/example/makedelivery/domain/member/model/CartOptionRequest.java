package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.CartOption;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CartOptionRequest {

    @NotEmpty(message = "가격은 공란일 수 없습니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull
    private Long menuOptionId;

    @NotNull
    private String menuOptionName;

    public static CartOption toEntity(CartOptionRequest request, Long cartId, Long memberId) {
        return CartOption.builder()
                .price(request.getPrice())
                .cartId(cartId)
                .memberId(memberId)
                .menuOptionId(request.getMenuOptionId())
                .menuOptionName(request.getMenuOptionName())
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
