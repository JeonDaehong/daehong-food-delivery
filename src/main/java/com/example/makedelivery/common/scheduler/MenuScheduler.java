package com.example.makedelivery.common.scheduler;

import com.example.makedelivery.domain.menu.service.MenuGroupService;
import com.example.makedelivery.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuScheduler {

    private final MenuService menuService;
    private final MenuGroupService menuGroupService;

    @Scheduled(fixedDelay = 1000 * 60 * 60) // 60분에 한 번 실행
    public void deleteAllAddressDeleteStatus() {
        menuService.deleteAllMenuDeleteStatus();
        menuGroupService.deleteAllMenuGroupsDeleteStatus();
    }

}
