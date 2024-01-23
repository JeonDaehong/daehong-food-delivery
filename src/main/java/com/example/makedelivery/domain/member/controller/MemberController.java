package com.example.makedelivery.domain.member.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.LoginService;
import com.example.makedelivery.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.makedelivery.common.constants.URIConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEMBER_API_URI)
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;

    /**
     * RequestParam 과 RequestBody 의 차이
     * RequestParam 은 기본 자료형이나 문자열 등의 단일 값 파라미터를 받을 때 사용하고,
     * RequestBody 는 복잡한 객체나 데이터를 Java Object 형식으로 변환하여 보낼 때 사용합니다.
     */
    @PostMapping("/join")
    public ResponseEntity<HttpStatus> join(@RequestBody @Valid MemberJoinRequest memberRequest) {
        // API 요청 상황에서 예기치 못한 상황이 있을 수 있으므로, 이메일 중복을 한 번 더 체크해 줌.
        memberService.existsByEmail(memberRequest.getEmail());
        memberService.join(memberRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/email/{email}/check")
    public ResponseEntity<HttpStatus> existsByEmail(@PathVariable String email) {
        memberService.existsByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid LoginRequest request) {
        // 해당 유저가 존재하는지, 그리고 해당 유저의 Status 가 Default 상태 인지를 확인한다.
        memberService.isValidMember(request);
        memberService.isValidStatus(request.getEmail());
        loginService.loginMember(memberService.findMemberByEmail(request.getEmail()).getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/logout")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> logout(@CurrentMember Member member) {
        loginService.logoutMember(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
