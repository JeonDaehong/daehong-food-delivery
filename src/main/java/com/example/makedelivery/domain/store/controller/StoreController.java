package com.example.makedelivery.domain.store.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.StoreInfoUpdateRequest;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.service.StoreService;
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
@RequestMapping(STORE_API_URI)
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/insert")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addStore(@RequestBody @Valid StoreInsertRequest request, @CurrentMember Member member)  {
        storeService.addStore(request, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<List<StoreResponse>> getMyAllStore(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(storeService.getMyAllStore(member));
    }

    @GetMapping("/{storeId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<StoreResponse> getMyStore(@CurrentMember Member member, @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member); // 내 매장이 맞는지 Check
        return ResponseEntity.status(HttpStatus.OK).body(storeService.getMyStore(storeId, member));
    }

    @PatchMapping("/{storeId}/opened")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> openMyStore(@CurrentMember Member member, @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        storeService.openMyStore(storeId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{storeId}/closed")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> closeMyStore(@CurrentMember Member member, @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        storeService.closeMyStore(storeId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{storeId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateStoreInformation(@CurrentMember Member member,
                                                             @RequestBody @Valid StoreInfoUpdateRequest request,
                                                             @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        storeService.updateStoreInformation(storeId, request, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{storeId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteStore(@CurrentMember Member member, @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        storeService.deleteStore(storeId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
