package com.example.makedelivery.domain.member.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.*;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.LoginService;
import com.example.makedelivery.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.util.HttpStatusResponseConstants.*;
import static com.example.makedelivery.common.util.URIConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEMBER_API_URI)
public class MemberInfoController {

    private final MemberService memberService;
    private final LoginService loginService;

    @GetMapping("/profile")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<MemberProfileResponse> getMemberInfoPage(@CurrentMember Member member) {
        return ResponseEntity.ok(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @PatchMapping("/update/profile")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<MemberProfileResponse> updateMemberProfile(@CurrentMember Member member, @RequestBody @Valid MemberProfileRequest request) {
        memberService.updateMemberProfile(member, request);
        return ResponseEntity.ok(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @PatchMapping("/update/password")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> updateMemberPassword(@CurrentMember Member member, @RequestBody @Valid MemberPasswordRequest request) {
        if (memberService.isValidPassword(member, request.getOldPassword())) return RESPONSE_BAD_REQUEST;
        memberService.updateMemberPassword(member, request);
        return RESPONSE_OK;
    }

    @DeleteMapping("/delete")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteMember(@CurrentMember Member member, @RequestParam String inputPassword) {
        if (memberService.isValidPassword(member, inputPassword)) return RESPONSE_BAD_REQUEST;
        memberService.deleteMember(member);
        loginService.logoutMember();
        return RESPONSE_OK;
    }


    @PostMapping("/address/add")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> addAddress(@CurrentMember Member member, @RequestBody @Valid MemberAddressRequest request) {
        memberService.addAddress(member, request);
        return RESPONSE_OK;
    }

    @PostMapping("/address/delete/{addressId}")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteAddress(@CurrentMember Member member, @PathVariable Long addressId) {
        if ( memberService.deleteAddress(member, addressId) ) return RESPONSE_OK;
        return RESPONSE_CONFLICT;
    }

    /**
     * 해당 주소를 메인 주소로 지정합니다.
     */
    @PostMapping("/address/{addressId}/change")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> changeMyMainAddress(@CurrentMember Member member, @PathVariable Long addressId) {
        memberService.updateMemberAddress(member, addressId);
        return RESPONSE_OK;
    }

    /**
     * 등록한 모든 주소 정보를 가져옵니다.
     */
    @GetMapping("/address")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<List<MemberAddressResponse>> getMyAddressList(@CurrentMember Member member) {
        return ResponseEntity.ok(memberService.getMyAddressList(member));
    }

}
