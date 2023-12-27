package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.OptionRequest;
import com.example.makedelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OptionApplicationService {

    private final OptionService optionService;
    private final StoreService storeService;

    @Transactional
    public void addOption(OptionRequest request, Member member, Long storeId, Long menuId) {
        storeService.validationCheckedMyStore(storeId, member);
        optionService.addOption(request, menuId);
    }

    @Transactional
    public void updateOption(OptionRequest request, Member member, Long storeId, Long menuId, Long optionId) {
        storeService.validationCheckedMyStore(storeId, member);
        optionService.updateOption(request, menuId, optionId);
    }

    @Transactional
    public void deleteOption(Member member, Long storeId, Long menuId, Long optionId) {
        storeService.validationCheckedMyStore(storeId, member);
        optionService.deleteOption(menuId, optionId);
    }

    @Transactional
    public void changeStatusHidden(Member member, Long storeId, Long menuId, Long optionId) {
        storeService.validationCheckedMyStore(storeId, member);
        optionService.changeStatusHidden(menuId, optionId);
    }

    @Transactional
    public void changeStatusDefault(Member member, Long storeId, Long menuId, Long optionId) {
        storeService.validationCheckedMyStore(storeId, member);
        optionService.changeStatusDefault(menuId, optionId);
    }

}
