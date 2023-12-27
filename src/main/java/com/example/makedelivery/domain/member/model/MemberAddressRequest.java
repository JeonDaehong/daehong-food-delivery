package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.model.entity.MemberAddress.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberAddressRequest {

    @NotEmpty(message = "주소는 공란일 수 없습니다.")
    private String address;

    @NotNull(message = "경도는 공란일 수 없습니다.")
    private Double longitude;

    @NotNull(message = "위도는 공란일 수 없습니다.")
    private Double latitude;

    public static MemberAddress toEntity(MemberAddressRequest request, Long memberId) {
        return MemberAddress.builder()
                .address(request.getAddress())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .memberId(memberId)
                .status(Status.DEFAULT) // 기본 상태
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();
    }

}
