package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import lombok.*;

@Builder
@Getter
public class MemberAddressResponse {

    private String address;
    private double longitude;
    private double latitude;

    public static MemberAddressResponse toMemberAddressResponse(MemberAddress memberAddress) {
        return MemberAddressResponse.builder()
                .address(memberAddress.getAddress())
                .longitude(memberAddress.getLongitude())
                .latitude(memberAddress.getLatitude())
                .build();
    }

}
