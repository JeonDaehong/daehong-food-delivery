package com.example.makedelivery.domain.menu.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuGroupResponse;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.service.MenuApplicationService;
import com.example.makedelivery.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.constants.URIConstants.*;

/**
 * MenuService 와 StoreService 를 모두 불러서 사용해야 하는 경우,
 * MenuApplicationService 라는 서비스를 만들어서 묶어주는 것이 좋습니다.
 * 이렇게 여러개의 서비스를 조합하여 사용할 때는 새로운 서비스를 만들어줍니다.
 * 그 이유는 트랜잭션 문제가 될 수 있고 ( 한 번에 롤백을 해야하는 경우 ),
 * 높은 응집도와 낮은 결합도 그리고 비즈니스 로직 중복을 최소화 하기 위한 이유도 있습니다.
 * 기본적으로 하나의 컨트롤러에서는 하나의 서비스만을 호출하는 것이 좋습니다.
 *
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MENU_API_URI)
public class MenuController {

    private final MenuApplicationService menuApplicationService; // MenuService + StoreService
    private final MenuService menuService;


    @PostMapping
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addMenu(@CurrentMember Member member,
                                              @RequestBody @Valid MenuRequest request,
                                              @PathVariable Long storeId) {
        menuApplicationService.addMenu(storeId, member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{menuId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateMenuInformation(@CurrentMember Member member,
                                                            @RequestBody @Valid MenuRequest request,
                                                            @PathVariable Long menuId,
                                                            @PathVariable Long storeId) {
        menuApplicationService.updateMenuInformation(storeId, menuId, member, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{menuId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteMenu(@CurrentMember Member member,
                                                 @PathVariable Long menuId,
                                                 @PathVariable Long storeId) {
        menuApplicationService.deleteMenu(storeId, menuId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{menuId}/changeHidden")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusHidden(@CurrentMember Member member,
                                                         @PathVariable Long menuId,
                                                         @PathVariable Long storeId) {
        menuApplicationService.changeStatusHidden(storeId, menuId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{menuId}/changeDefault")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusDefault(@CurrentMember Member member,
                                                          @PathVariable Long menuId,
                                                          @PathVariable Long storeId) {
        menuApplicationService.changeStatusDefault(storeId, menuId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 메뉴 그룹 안에,
     * 메뉴가 담긴 채로 반환됩니다.
     */
    @GetMapping
    public ResponseEntity<List<MenuGroupResponse>> getStoreMenuList(@PathVariable Long storeId) {
        List<MenuGroupResponse> menuList = menuService.getMenuList(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(menuList);
    }

}
