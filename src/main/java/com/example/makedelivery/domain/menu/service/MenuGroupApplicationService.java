package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuGroupRequest;
import com.example.makedelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuGroupApplicationService {

    private final MenuGroupService menuGroupService;
    private final StoreService storeService;

    @Transactional
    public void addMenuGroup(Long storeId, Member member, MenuGroupRequest request) {
        storeService.validationCheckedMyStore(storeId, member);
        menuGroupService.addMenuGroup(request, storeId);
    }

    @Transactional
    public void updateMenuGroupInformation(Long storeId, Member member, Long menuGroupId, MenuGroupRequest request) {
        storeService.validationCheckedMyStore(storeId, member);
        menuGroupService.updateMenuGroupInformation(menuGroupId, request);
    }

    @Transactional
    public void deleteMenuGroup(Long storeId, Member member, Long menuGroupId) {
        storeService.validationCheckedMyStore(storeId, member);
        menuGroupService.deleteMenuGroup(menuGroupId);
    }

}
