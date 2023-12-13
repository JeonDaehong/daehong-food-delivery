package com.example.makedelivery.domain.menu.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuGroupRequest;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.service.MenuGroupService;
import com.example.makedelivery.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.makedelivery.common.util.HttpStatusResponseConstants.RESPONSE_CONFLICT;
import static com.example.makedelivery.common.util.HttpStatusResponseConstants.RESPONSE_OK;
import static com.example.makedelivery.common.util.URIConstants.MENU_GROUP_API_URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MENU_GROUP_API_URI)
public class MenuGroupController {

    private final MenuGroupService menuGroupService;
    private final StoreService storeService;

    @PostMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addMenuGroup(@CurrentMember Member member,
                                                   @RequestParam @Valid MenuGroupRequest request,
                                                   @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuGroupService.addMenuGroup(request, storeId);
        return RESPONSE_OK;
    }

    @PatchMapping("/{menuGroupId}/update")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateMenuGroupInformation(@CurrentMember Member member,
                                                                 @RequestParam @Valid MenuGroupRequest request,
                                                                 @PathVariable Long menuGroupId,
                                                                 @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuGroupService.updateMenuGroupInformation(menuGroupId, request);
        return RESPONSE_OK;
    }

    @DeleteMapping("/{menuGroupId}/delete")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteMenuGroup(@CurrentMember Member member,
                                                      @PathVariable Long menuGroupId,
                                                      @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        if ( menuGroupService.deleteMenuGroup(menuGroupId) ) return RESPONSE_OK;
        return RESPONSE_CONFLICT;
    }

}
