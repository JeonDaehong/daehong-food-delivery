package com.example.makedelivery.domain.member.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberAddressPriorityRequest {

    @NotNull(message = "주소 아이디는 비어있을 수 없습니다.")
    private Long addressId;
    
    @NotNull(message = "순서는 비어있을 수 없습니다.")
    private Integer priority;
    
}
