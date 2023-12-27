package com.example.makedelivery.common.scheduler;

import com.example.makedelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreScheduler {

    private final StoreService storeService;

    @Scheduled(fixedDelay = 1000 * 60 * 60) // 매장 삭제
    public void deleteAllStoreDeleteStatus() {
        storeService.deleteAllStoreDeleteStatus();
    }

}
