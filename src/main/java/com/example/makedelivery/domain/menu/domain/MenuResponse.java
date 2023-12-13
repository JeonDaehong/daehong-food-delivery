package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.store.model.StoreResponse;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MenuResponse {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    private Long storeId;
    private String imageFileName;

    public static MenuResponse toMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .imageFileName(menu.getImageFileName())
                .build();
    }

}
