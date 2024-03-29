package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.store.model.StoreResponse;
import lombok.*;

@Builder
@Getter
@ToString
public class MenuResponse {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String awsImagePathURL;

    public static MenuResponse toMenuResponse(Menu menu, String awsImagePathURL) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .awsImagePathURL(awsImagePathURL)
                .build();
    }

}
