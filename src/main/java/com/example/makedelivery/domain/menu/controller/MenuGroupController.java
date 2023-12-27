package com.example.makedelivery.domain.menu.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuGroupRequest;
import com.example.makedelivery.domain.menu.service.MenuGroupApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.makedelivery.common.constants.URIConstants.MENU_GROUP_API_URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MENU_GROUP_API_URI)
public class MenuGroupController {

    private final MenuGroupApplicationService menuGroupApplicationService; // MenuGroupService + StoreService

    @PostMapping
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addMenuGroup(@CurrentMember Member member,
                                                   @RequestParam @Valid MenuGroupRequest request,
                                                   @PathVariable Long storeId) {
        menuGroupApplicationService.addMenuGroup(storeId, member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{menuGroupId}")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateMenuGroupInformation(@CurrentMember Member member,
                                                                 @RequestParam @Valid MenuGroupRequest request,
                                                                 @PathVariable Long menuGroupId,
                                                                 @PathVariable Long storeId) {
        menuGroupApplicationService.updateMenuGroupInformation(storeId, member, menuGroupId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{menuGroupId}")
    @LoginCheck(memberLevel = LoginCheck.MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteMenuGroup(@CurrentMember Member member,
                                                      @PathVariable Long menuGroupId,
                                                      @PathVariable Long storeId) {
        menuGroupApplicationService.deleteMenuGroup(storeId, member, menuGroupId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
