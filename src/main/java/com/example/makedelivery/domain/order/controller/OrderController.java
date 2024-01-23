package com.example.makedelivery.domain.order.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.order.domain.OrderMenuRequest;
import com.example.makedelivery.domain.order.domain.OrderResponse;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentType;
import com.example.makedelivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.constants.URIConstants.ORDER_API_URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ORDER_API_URI)
public class OrderController {

    private final OrderService orderService;

    /**
     * 매장 -> 메뉴에서 즉시 주문은 안되며,
     * 주문은 반드시 장바구니를 거쳐서만
     * 주문을 할 수 있도록 설계하였습니다.
     */
    @PostMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> registerOrder(@CurrentMember Member member,
                                                    @RequestBody @Valid List<OrderMenuRequest> requestList,
                                                    @RequestBody PaymentType type,
                                                    @RequestParam Long addressId,
                                                    @RequestParam Long storeId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer usePoint) {
        orderService.registerOrder(member, requestList, type, addressId, storeId, usePoint);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> cancelOrderByMember(@CurrentMember Member member,
                                                          @RequestParam Long orderId,
                                                          @RequestParam Long orderStoreId) {
        orderService.cancelOrderByMember(member, orderId, orderStoreId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{storeId}/storeOrder")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> cancelOrderByStore(@CurrentMember Member member,
                                                         @RequestParam Long orderId,
                                                         @PathVariable Long storeId) {
        orderService.cancelOrderByStore(member, orderId, storeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.MEMBER)
    public ResponseEntity<List<OrderResponse>> getMyOrderList(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getMyOrderList(member));
    }

    @GetMapping("/{storeId}/storeOrder")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<List<OrderResponse>> getMyStoreOrderList(@CurrentMember Member member,
                                                                   @PathVariable Long storeId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getMyStoreOrderList(member, storeId));
    }

    @PutMapping("/{storeId}/{orderId}/changeApprove")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeOrderStatusApprove(@CurrentMember Member member,
                                                               @PathVariable Long storeId,
                                                               @PathVariable Long orderId) {
        orderService.changeOrderStatusApprove(member, orderId, storeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{storeId}/{orderId}/changeDeliveryWait")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeOrderStatusDeliveryWait(@CurrentMember Member member,
                                                                    @PathVariable Long storeId,
                                                                    @PathVariable Long orderId) {
        orderService.changeOrderStatusDeliveryWait(member, orderId, storeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
