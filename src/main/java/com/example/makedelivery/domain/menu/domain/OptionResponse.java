package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Option;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OptionResponse {

    private Long id;
    private String name;
    private Integer price;
    private Long menuId;

    public static OptionResponse toOptionResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .price(option.getPrice())
                .menuId(option.getMenuId())
                .build();
    }

}
