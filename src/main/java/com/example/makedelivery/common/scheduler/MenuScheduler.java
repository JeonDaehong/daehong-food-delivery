package com.example.makedelivery.common.scheduler;

import com.example.makedelivery.domain.menu.service.MenuGroupService;
import com.example.makedelivery.domain.menu.service.MenuService;
import com.example.makedelivery.domain.menu.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuScheduler {

    private final OptionService optionService;
    private final MenuService menuService;
    private final MenuGroupService menuGroupService;

    @Scheduled(fixedDelay = 1000 * 60 * 60) // 메뉴 + 메뉴 그룹 삭제
    public void deleteAllMenuDeleteStatus() {
        menuService.deleteAllMenuDeleteStatus();
        menuGroupService.deleteAllMenuGroupsDeleteStatus();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60) // 옵션 삭제
    public void deleteAllOptionDeleteStatus() {
        optionService.deleteAllOptionDeleteStatus();
    }

}
