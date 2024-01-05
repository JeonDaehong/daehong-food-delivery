package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Menu.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
public class MenuRequest {

    @NotEmpty(message = "메뉴 이름은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "메뉴 이름은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private String name;

    @NotNull(message = "가격은 필수 입력입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotEmpty(message = "메뉴 설명은 공란일 수 없습니다.")
    private String description;

    @NotEmpty(message = "메뉴 그룹 설정은 필수입니다.")
    private Long menuGroupId;

    public static Menu toEntity(MenuRequest request, String imageFileName) {
        return Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .menuGroupId(request.getMenuGroupId())
                .status(Status.DEFAULT)
                .imageFileName(imageFileName)
                .build();
    }

}
