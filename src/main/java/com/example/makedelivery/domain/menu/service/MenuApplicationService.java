package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ApplicationService 는, MenuService 와 StoreService 를 동시에 처리하는 것처럼,
 * 두 가지 종류의 서비스 처리를 묶어서 처리 할 경우 사용합니다.
 * Controller 에서 MenuService 와 StoreService 를 모두 불러 사용하는 것보다
 * 이렇게 MenuApplicationService 라는 중간 단계를 만듦으로써 서비스를 조합하는 것이 좋습니다.
 * 그 이유는 트랜잭션 문제가 될 수 있고 ( 한 번에 롤백을 해야하는 경우 ),
 * 높은 응집도와 낮은 결합도 그리고 비즈니스 로직 중복을 최소화 하기 위한 이유도 있습니다.
 * 기본적으로 하나의 컨트롤러에서는 하나의 서비스만을 호출하는 것이 좋습니다.
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MenuApplicationService {

    private final MenuService menuService;
    private final StoreService storeService;

    /**
     * Transactional 의 기본 상태는 @Transactional(propagation = Propagation.REQUIRED) 입니다.
     * 그래서 이미 동작하고 있는 Transactional 이 있다면, 새로운 Transactional 을 동작시키지 않습니다.
     * 그 덕분에 아래처럼 Transactional 로 감싸진 2개의 메서드를 불러도,
     * 이미 Transactional 이 동작하고 있으므로, 해당 2개의 메서드는 같은 Transactional 안에 있습니다.
     * 즉, 아래 코드를 예시로 보았을 때 addMenu 메서드 자체가 하나의 원자성이 보장되므로
     * menuService.addMenu(request); 에서 문제가 생겨도,
     * storeService.validationCheckedMyStore(storeId, member); 도 롤백이 가능합니다.
     * ( 물론, 아래 코드의 storeService.validationCheckedMyStore(storeId, member); 는 check 하는 메서드라 무관하지만,
     *   DB write 하는 메서드라 가정하였을 때도 롤백이 가능합니다. )
     */
    @Transactional
    public void addMenu(Long storeId, Member member, MenuRequest request) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.addMenu(request);
    }

    @Transactional
    public void updateMenuInformation(Long storeId, Long menuId, Member member, MenuRequest request) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.updateMenuInformation(menuId, request);
    }

    @Transactional
    public void deleteMenu(Long storeId, Long menuId, Member member) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.deleteMenu(menuId);
    }

    @Transactional
    public void changeStatusHidden(Long storeId, Long menuId, Member member) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.changeStatusHidden(menuId);
    }

    @Transactional
    public void changeStatusDefault(Long storeId, Long menuId, Member member) {
        storeService.validationCheckedMyStore(storeId, member);
        menuService.changeStatusDefault(menuId);
    }
}
