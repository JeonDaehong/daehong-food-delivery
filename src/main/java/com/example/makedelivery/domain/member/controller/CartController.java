package com.example.makedelivery.domain.member.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.domain.member.model.CartRequest;
import com.example.makedelivery.domain.member.model.CartResponse;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.CartService;
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
@RequestMapping(MEMBER_CART_API_URI)
public class CartController {

    private final CartService cartService;

    @PostMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> addCart(@CurrentMember Member member,
                                              @RequestBody @Valid CartRequest request) {
        cartService.addCart(member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<List<CartResponse>> loadMyCart(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.loadCart(member));
    }

    @DeleteMapping("/{cartId}")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteCart(@CurrentMember Member member,
                                                 @PathVariable Long cartId) {
        cartService.deleteCart(member, cartId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping()
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> deleteCartAll(@CurrentMember Member member) {
        cartService.deleteCartAll(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
