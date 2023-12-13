package com.example.makedelivery.domain.member.controller;

import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.MemberAddressRequest;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.service.LoginService;
import com.example.makedelivery.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.makedelivery.common.util.HttpStatusResponseConstants.*;
import static com.example.makedelivery.common.util.URIConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEMBER_API_URI)
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;

    @PostMapping("/join")
    public ResponseEntity<HttpStatus> join(@RequestBody @Valid MemberJoinRequest memberRequest) {
        // API 요청 상황에서 예기치 못한 상황이 있을 수 있으므로, 이메일 중복을 한 번 더 체크해 줌.
        if (memberService.existsByEmail(memberRequest.getEmail())) return RESPONSE_CONFLICT;
        memberService.join(memberRequest);
        return RESPONSE_OK;
    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<HttpStatus> existsByEmail(@PathVariable String email) {
        if (memberService.existsByEmail(email)) return RESPONSE_CONFLICT;
        return RESPONSE_OK;
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid LoginRequest request) {
        // 해당 유저가 존재하는지, 그리고 해당 유저의 Status 가 Default 상태 인지를 확인한다.
        if (memberService.isValidMember(request) && memberService.isValidStatus(request.getEmail())) {
            loginService.loginMember(memberService.findMemberByEmail(request.getEmail()).getId());
            return RESPONSE_OK;
        }
        return RESPONSE_BAD_REQUEST;
    }

    @GetMapping("/logout")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> logout() {
        loginService.logoutMember();
        return RESPONSE_OK;
    }

}
