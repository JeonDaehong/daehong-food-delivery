package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MenuGroupRequest {

    @NotEmpty(message = "그룹 이름은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "메뉴 이름은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private String name;

    public static MenuGroup toEntity(MenuGroupRequest request, Long storeId) {
        return MenuGroup.builder()
                .name(request.getName())
                .storeId(storeId)
                .status(Status.DEFAULT)
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
