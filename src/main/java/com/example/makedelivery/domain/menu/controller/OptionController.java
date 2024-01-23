package com.example.makedelivery.domain.menu.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.OptionRequest;
import com.example.makedelivery.domain.menu.domain.OptionResponse;
import com.example.makedelivery.domain.menu.service.OptionApplicationService;
import com.example.makedelivery.domain.menu.service.OptionService;
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
@RequestMapping(MENU_OPTION_API_URI)
public class OptionController {

    private final OptionApplicationService optionApplicationService; // OptionService + StoreService
    private final OptionService optionService;

    @PostMapping
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> addOption(@CurrentMember Member member,
                                                @PathVariable Long storeId,
                                                @PathVariable Long menuId,
                                                @RequestBody @Valid OptionRequest request) {
        optionApplicationService.addOption(request, member, storeId, menuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{optionId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> updateOption(@CurrentMember Member member,
                                                   @PathVariable Long storeId,
                                                   @PathVariable Long menuId,
                                                   @PathVariable Long optionId,
                                                   @RequestBody @Valid OptionRequest request) {
        optionApplicationService.updateOption(request, member, storeId, menuId, optionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{optionId}")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> deleteOption(@CurrentMember Member member,
                                                   @PathVariable Long storeId,
                                                   @PathVariable Long menuId,
                                                   @PathVariable Long optionId) {
        optionApplicationService.deleteOption(member, storeId, menuId, optionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{optionId}/changeHidden")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusHidden(@CurrentMember Member member,
                                                         @PathVariable Long storeId,
                                                         @PathVariable Long menuId,
                                                         @PathVariable Long optionId) {
        optionApplicationService.changeStatusHidden(member, storeId, menuId, optionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{optionId}/changeDefault")
    @LoginCheck(memberLevel = MemberLevel.OWNER)
    public ResponseEntity<HttpStatus> changeStatusDefault(@CurrentMember Member member,
                                                          @PathVariable Long storeId,
                                                          @PathVariable Long menuId,
                                                          @PathVariable Long optionId) {
        optionApplicationService.changeStatusDefault(member, storeId, menuId, optionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 해당 메뉴의 옵션은, 메뉴 그룹에 메뉴가 담겨오는 것과는 다르게,
     * 해당 메뉴 정보를 들어간 후에, 메뉴에 적용 가능한 옵션 리스트를 확인할 수 있도록 설계하였습니다.
     * 그래서 MenuResponse 안에 List<OptionResponse> 를 담지 않았습니다.
     */
    @GetMapping
    public ResponseEntity<List<OptionResponse>> getMenuOptions(@PathVariable Long storeId,
                                                               @PathVariable Long menuId) {
        List<OptionResponse> optionList = optionService.getMenuOptions(menuId);
        return ResponseEntity.status(HttpStatus.OK).body(optionList);
    }

}
