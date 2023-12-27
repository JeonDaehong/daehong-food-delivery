package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.menu.domain.MenuGroupResponse;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.domain.MenuResponse;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.repository.MenuGroupRepository;
import com.example.makedelivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;

    /**
     * 에러를 반환하는 공통 코드를 하나의 함수로 통일하였습니다. ( 중복되므로 )
     */
    private Menu getMenuByIdOrThrowException(Long menuId) {
        return menuRepository.findMenuById(menuId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.MENU_NOT_FOUND));
    }

    @Transactional
    public void addMenu(MenuRequest request) {
        Menu menu = MenuRequest.toEntity(request);
        menuRepository.save(menu);
    }

    @Transactional
    public void updateMenuInformation(Long menuId, MenuRequest request) {
        Menu menu = getMenuByIdOrThrowException(menuId);
        menu.updateMenuInfo(request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getMenuGroupId(),
                request.getImageFileName(),
                LocalDateTime.now());
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = getMenuByIdOrThrowException(menuId);
        menu.deleteMenu(LocalDateTime.now());
    }

    @Transactional
    public void changeStatusHidden(Long menuId) {
        Menu menu = getMenuByIdOrThrowException(menuId);
        menu.changeStatusHidden(LocalDateTime.now());

    }

    @Transactional
    public void changeStatusDefault(Long menuId) {
        Menu menu = getMenuByIdOrThrowException(menuId);
        menu.changeStatusDefault(LocalDateTime.now());

    }

    /**
     * 해당 매장의 모든 메뉴 그룹들을 가져옵니다.
     * 이 때, 메뉴 그룹에 속한 메뉴들을 담습니다.
     * ( Default 상태인 메뉴 그룹들만 담습니다. )
     */
    @Transactional(readOnly = true)
    public List<MenuGroupResponse> getMenuList(Long storeId) {
        return menuGroupRepository.findAllByStoreIdAndStatusOrderByName(storeId, MenuGroup.Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(menuGroup -> MenuGroupResponse.toMenuGroupResponse(menuGroup, getMenuListByMenuGroup(menuGroup.getId())))
                .toList();
    }

    /**
     * 그룹 안에 있는 메뉴들을 찾습니다. ( Default 상태인 메뉴들만 담습니다. )
     */
    @Transactional(readOnly = true)
    public List<MenuResponse> getMenuListByMenuGroup(Long menuId) {
        return menuRepository.findAllByMenuGroupIdAndStatusOrderByName(menuId, Menu.Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(MenuResponse::toMenuResponse)
                .toList();
    }

    /**
     * 스케쥴러를 통해 상태가 Deleted 인 메뉴를 삭제해 줍니다.
     * 상태를 변경하지 않고, 바로 삭제를 하면
     * 현재 진행중인 다른 로직에 영향을 받을 수 있기 때문에
     * 이렇게 지연 삭제처리를 해줍니다.
     */
    @Transactional
    public void deleteAllMenuDeleteStatus() {
        menuRepository.deleteAllByStatus(Menu.Status.DELETED);
    }

}
