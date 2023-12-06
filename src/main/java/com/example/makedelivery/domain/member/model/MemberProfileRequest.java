package com.example.makedelivery.domain.member.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class MemberProfileRequest {

    @NotEmpty(message = "닉네임은 공란일 수 없습니다.")
    private final String nickname;

}
