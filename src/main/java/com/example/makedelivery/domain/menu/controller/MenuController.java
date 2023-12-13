package com.example.makedelivery.domain.menu.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuGroupResponse;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.domain.MenuResponse;
import com.example.makedelivery.domain.menu.service.MenuService;
import com.example.makedelivery.domain.store.service.StoreService;
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
@RequestMapping(MENU_API_URI)
public class MenuController {

    private final MenuService menuService;
    private final StoreService storeService;

    @PostMapping
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addMenu(@CurrentMember Member member,
                                              @RequestParam @Valid MenuRequest request,
                                              @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.addMenu(request);
        return RESPONSE_OK;
    }

    @PatchMapping("/{menuId}/update")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateMenuInformation(@CurrentMember Member member,
                                                            @RequestParam @Valid MenuRequest request,
                                                            @PathVariable Long menuId,
                                                            @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.updateMenuInformation(menuId, request);
        return RESPONSE_OK;
    }

    @DeleteMapping("/{menuId}/delete")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteMenu(@CurrentMember Member member,
                                                 @PathVariable Long menuId,
                                                 @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.deleteMenu(menuId);
        return RESPONSE_OK;
    }

    @PatchMapping("/{menuId}/changeHidden")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusHidden(@CurrentMember Member member,
                                                         @PathVariable Long menuId,
                                                         @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.changeStatusHidden(menuId);
        return RESPONSE_OK;
    }

    @PatchMapping("/{menuId}/changeDefault")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusDefault(@CurrentMember Member member,
                                                          @PathVariable Long menuId,
                                                          @PathVariable Long storeId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.changeStatusDefault(menuId);
        return RESPONSE_OK;
    }

    /**
     * 메뉴 그룹 안에,
     * 메뉴가 담긴 채로 반환됩니다.
     */
    @GetMapping
    public ResponseEntity<List<MenuGroupResponse>> getStoreMenuList(@PathVariable Long storeId) {
        List<MenuGroupResponse> menuList = menuService.getMenuList(storeId);
        return ResponseEntity.ok(menuList);
    }

}
