package com.example.makedelivery.domain.rider.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.facade.OptimisticLockFacade;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.rider.domain.DeliveryHistoryResponse;
import com.example.makedelivery.domain.rider.domain.RiderPossibleOrderResponse;
import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory.DeliveryStatus;
import com.example.makedelivery.domain.rider.service.RiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.constants.URIConstants.RIDER_API_URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(RIDER_API_URI)
public class RiderController {

    private final OptimisticLockFacade optimisticLockFacade; // 배차 : 낙관적 락
    private final RiderService riderService;

    @GetMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<List<RiderPossibleOrderResponse>> getRiderPossibleOrder(@CurrentMember Member member,
                                                                                  @RequestParam Double myLatitude,
                                                                                  @RequestParam Double myLongitude) {
        return ResponseEntity.status(HttpStatus.OK).body(riderService.getRiderPossibleOrder(member, myLatitude, myLongitude));
    }

    @PostMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<HttpStatus> registerRider(@CurrentMember Member member,
                                                    @RequestParam Long orderId) throws InterruptedException {
        optimisticLockFacade.registerRider(member, orderId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<HttpStatus> cancelRider(@CurrentMember Member member,
                                                  @RequestParam Long deliveryId) {
        riderService.cancelRider(member, deliveryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<HttpStatus> completedDelivery(@CurrentMember Member member,
                                                        @RequestParam Long deliveryId) {
        riderService.completedDelivery(member, deliveryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/DeliveringHistory")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<List<DeliveryHistoryResponse>> getMyDeliveringHistory(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(riderService.getMyDeliveryHistory(member, DeliveryStatus.DELIVERING));
    }

    @GetMapping("/completedDeliveryHistory")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.RIDER)
    public ResponseEntity<List<DeliveryHistoryResponse>> getMyCompletedDeliveryHistory(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(riderService.getMyDeliveryHistory(member, DeliveryStatus.COMPLETED));
    }

}
