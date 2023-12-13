package com.example.makedelivery.domain.member.model;

import com.example.makedelivery.domain.member.model.entity.Member;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MemberProfileResponse {

    private String email;
    private String nickname;

    public static MemberProfileResponse toMemberProfileResponse(Member member) {
        return MemberProfileResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
