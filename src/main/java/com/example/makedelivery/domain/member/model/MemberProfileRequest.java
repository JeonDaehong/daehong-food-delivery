package com.example.makedelivery.domain.member.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class MemberProfileRequest {

    @NotEmpty(message = "닉네임은 공란일 수 없습니다.")
    @Size(min = 2, max = 30, message = "닉네임은 최소 2자 이상, 최대 30자 이하여야 합니다.")
    private final String nickname;

}
