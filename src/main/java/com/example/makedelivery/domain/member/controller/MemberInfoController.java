package com.example.makedelivery.domain.member.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.MemberPasswordRequest;
import com.example.makedelivery.domain.member.model.MemberProfileResponse;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.LoginService;
import com.example.makedelivery.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.makedelivery.common.util.HttpStatusResponseConstants.RESPONSE_BAD_REQUEST;
import static com.example.makedelivery.common.util.HttpStatusResponseConstants.RESPONSE_OK;
import static com.example.makedelivery.common.util.URIConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEMBER_API_URI)
public class MemberInfoController {

    private final MemberService memberService;

    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> getMemberInfoPage(@CurrentMember Member member) {
        return ResponseEntity.ok(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    @PatchMapping("/update/profile")
    public ResponseEntity<MemberProfileResponse> updateMemberProfile(@CurrentMember Member member, @RequestBody @Valid MemberProfileRequest request) {
        memberService.updateMemberProfile(member, request);
        return ResponseEntity.ok(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    @PatchMapping("/update/password")
    public ResponseEntity<HttpStatus> updateMemberPassword(@CurrentMember Member member, @RequestBody @Valid MemberPasswordRequest request) {
        if ( !memberService.isValidPassword(member, request) ) return RESPONSE_BAD_REQUEST;
        memberService.updateMemberPassword(member, request);
        return RESPONSE_OK;
    }


}
