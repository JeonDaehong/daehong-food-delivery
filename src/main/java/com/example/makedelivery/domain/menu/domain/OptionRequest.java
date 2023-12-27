package com.example.makedelivery.domain.menu.domain;

import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.domain.entity.Option.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OptionRequest {

    @NotEmpty(message = "옵션 이름은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "옵션 이름은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private String name;

    @NotNull(message = "가격은 필수 입력입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    public static Option toEntity(OptionRequest request, Long menuId) {
        return Option.builder()
                .name(request.getName())
                .price(request.getPrice())
                .menuId(menuId)
                .status(Status.DEFAULT)
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
