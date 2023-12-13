package com.example.makedelivery.domain.store.model;

import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.OpenStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StoreInsertRequest {

    @NotEmpty(message = "매장명은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "매장명은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private String name;


    @NotEmpty(message = "휴대폰 번호는 공란일 수 없습니다.")
    @Pattern( message = "올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)",
               regexp = "^010-\\d{4}-\\d{4}$")
    private String phone;

    @NotEmpty(message = "주소는 공란일 수 없습니다.")
    private String address;

    @NotEmpty(message = "매장 소개는 공란일 수 없습니다.")
    private String introduction;

    @NotNull
    private Long categoryId;

    @NotNull(message = "경도는 공란일 수 없습니다.")
    private Double longitude;

    @NotNull(message = "위도는 공란일 수 없습니다.")
    private Double latitude;

    public static Store toEntity(StoreInsertRequest request, Long ownerId) {
        return Store.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .ownerId(ownerId)
                .openStatus(OpenStatus.CLOSED) // 생성 시 기본은 폐점 상태
                .status(Store.Status.DEFAULT) // 기본 생성은 정상 상태
                .introduction(request.getIntroduction())
                .categoryId(request.categoryId)
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
