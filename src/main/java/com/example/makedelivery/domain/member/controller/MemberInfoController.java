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

import static com.example.makedelivery.common.constants.URIConstants.*;

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
        return ResponseEntity.status(HttpStatus.OK).body(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @PatchMapping("/profile")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<MemberProfileResponse> updateMemberProfile(@CurrentMember Member member, @RequestBody @Valid MemberProfileRequest request) {
        memberService.updateMemberProfile(member, request);
        return ResponseEntity.status(HttpStatus.OK).body(MemberProfileResponse.toMemberProfileResponse(member));
    }

    @PatchMapping("/password")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> updateMemberPassword(@CurrentMember Member member, @RequestBody @Valid MemberPasswordRequest request) {
        memberService.isValidPassword(member, request.getOldPassword());
        memberService.updateMemberPassword(member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/profile")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteMember(@CurrentMember Member member, @RequestParam String inputPassword) {
        memberService.isValidPassword(member, inputPassword);
        memberService.deleteMember(member);
        loginService.logoutMember();
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/address")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> addAddress(@CurrentMember Member member, @RequestBody @Valid MemberAddressRequest request) {
        memberService.addAddress(member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/address/{addressId}")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteAddress(@CurrentMember Member member, @PathVariable Long addressId) {
        memberService.deleteAddress(member, addressId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 해당 주소를 메인 주소로 지정합니다.
     */
    @PostMapping("/address/{addressId}")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> changeMyMainAddress(@CurrentMember Member member, @PathVariable Long addressId) {
        memberService.updateMemberAddress(member, addressId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 등록한 모든 주소 정보를 가져옵니다.
     */
    @GetMapping("/address")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<List<MemberAddressResponse>> getMyAddressList(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMyAddressList(member));
    }

}
