package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class MenuGroupResponse {

    private Long id;
    private String name;
    private Long storeId;
    private Status status;
    private List<MenuResponse> menuList; // 해당 그룹 속, 메뉴 리스트

    public static MenuGroupResponse toMenuGroupResponse(MenuGroup menuGroup, List<MenuResponse> menuList) {
        return MenuGroupResponse.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .storeId(menuGroup.getStoreId())
                .status(menuGroup.getStatus())
                .menuList(menuList)
                .build();
    }

}
