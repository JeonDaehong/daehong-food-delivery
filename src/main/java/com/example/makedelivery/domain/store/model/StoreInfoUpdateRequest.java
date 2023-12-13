package com.example.makedelivery.domain.store.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StoreInfoUpdateRequest {

    @NotEmpty(message = "매장명은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "닉네임은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private String name;

    @NotEmpty(message = "휴대폰 번호는 공란일 수 없습니다.")
    @Pattern( message = "올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)",
            regexp = "^010-\\d{4}-\\d{4}$")
    private String phone;

    @NotEmpty(message = "주소는 공란일 수 없습니다.")
    private String address;

    @NotEmpty(message = "매장 소개는 공란일 수 없습니다.")
    private String introduction;

    @NotNull(message = "경도는 공란일 수 없습니다.")
    private Double longitude;

    @NotNull(message = "위도는 공란일 수 없습니다.")
    private Double latitude;

}
