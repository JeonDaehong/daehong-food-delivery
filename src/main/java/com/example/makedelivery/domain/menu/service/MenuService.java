package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.domain.menu.domain.MenuGroupResponse;
import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.domain.MenuResponse;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Menu.Status;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.repository.MenuGroupRepository;
import com.example.makedelivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;

    @Transactional
    public void addMenu(MenuRequest request) {
        Menu menu = MenuRequest.toEntity(request);
        menuRepository.save(menu);
    }

    @Transactional
    public void updateMenuInformation(Long menuId, MenuRequest request) {
        Optional<Menu> menuOptional = menuRepository.findMenuById(menuId);
        if ( menuOptional.isPresent() ) {
            Menu menu = menuOptional.get();
            menu.updateMenuInfo(request.getName(),
                                request.getDescription(),
                                request.getPrice(),
                                request.getMenuGroupId(),
                                request.getImageFileName(),
                                LocalDateTime.now());
        }
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Optional<Menu> menuOptional = menuRepository.findMenuById(menuId);
        if ( menuOptional.isPresent() ) {
            Menu menu = menuOptional.get();
            menu.deleteMenu(LocalDateTime.now());
        }
    }

    @Transactional
    public void changeStatusHidden(Long menuId) {
        Optional<Menu> menuOptional = menuRepository.findMenuById(menuId);
        if ( menuOptional.isPresent() ) {
            Menu menu = menuOptional.get();
            menu.changeStatusHidden(LocalDateTime.now());
        }
    }

    @Transactional
    public void changeStatusDefault(Long menuId) {
        Optional<Menu> menuOptional = menuRepository.findMenuById(menuId);
        if ( menuOptional.isPresent() ) {
            Menu menu = menuOptional.get();
            menu.changeStatusDefault(LocalDateTime.now());
        }
    }

    /**
     * 해당 매장의 모든 메뉴 그룹들을 가져옵니다.
     * 이 때, 메뉴 그룹에 속한 메뉴들을 담습니다.
     */
    public List<MenuGroupResponse> getMenuList(Long storeId) {
        Optional<List<MenuGroup>> menuGroupListOptional = menuGroupRepository.findAllByStoreIdAndStatusOrderByName(storeId, MenuGroup.Status.DEFAULT);
        if ( menuGroupListOptional.isPresent() ) {
            List<MenuGroupResponse> menuGroupResponseList = new ArrayList<>();
            List<MenuGroup> menuGroupList = menuGroupListOptional.get();
            for ( MenuGroup menuGroup : menuGroupList ) {
                MenuGroupResponse menuGroupResponse = MenuGroupResponse.toMenuGroupResponse(menuGroup, getMenuListByMenuGroup(menuGroup.getId()));
                menuGroupResponseList.add(menuGroupResponse);
            }
            return menuGroupResponseList;
        }
        return null;
    }

    /**
     * 그룹 안에 있는 메뉴들을 찾습니다.
     */
    public List<MenuResponse> getMenuListByMenuGroup(Long menuId) {
        Optional<List<Menu>> menuListOptional = menuRepository.findAllByMenuGroupIdAndStatusOrderByName(menuId, Menu.Status.DEFAULT);
        if ( menuListOptional.isPresent() ) {
            List<MenuResponse> menuResponseList = new ArrayList<>();
            List<Menu> menuList = menuListOptional.get();
            for ( Menu menu : menuList ) {
                MenuResponse menuResponse = MenuResponse.toMenuResponse(menu);
                menuResponseList.add(menuResponse);
            }
            return menuResponseList;
        }
        return null;
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
