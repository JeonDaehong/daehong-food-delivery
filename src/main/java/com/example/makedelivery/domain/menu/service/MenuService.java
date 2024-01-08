package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.image.service.FileService;
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

    private final FileService fileService; // AWS S3 getFileURL

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;

    /**
     * MenuService 내에서만 사용한다면, private 로 선언을 하였겠지만,
     * 해당 메서드는 외부에서도 사용될 수 있으므로 public 으로 선언하였습니다. ( OrderService 에서 불러옴 )
     * 단, 내부 서비스에서 이 메서드가 불려올 경우에는
     * self-invocation 으로 @Transactional(readOnly = true) 은 동작하지 않습니다.
     */
    @Transactional(readOnly = true)
    public Menu getMenuById(Long menuId) {
        return menuRepository.findMenuById(menuId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.MENU_NOT_FOUND));
    }

    @Transactional
    public void addMenu(MenuRequest request, String imageFileName) {
        Menu menu = MenuRequest.toEntity(request, imageFileName);
        menuRepository.save(menu);
    }

    @Transactional
    public void updateMenuInformation(Long menuId, MenuRequest request) {
        Menu menu = getMenuById(menuId);
        menu.updateMenuInfo(request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getMenuGroupId());
    }

    @Transactional
    public void updateMenuImage(Long menuId, String imageFileName) {
        Menu menu = getMenuById(menuId);
        menu.updateMenuImage(imageFileName);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = getMenuById(menuId);
        menu.deleteMenu();
    }

    @Transactional
    public void changeStatusHidden(Long menuId) {
        Menu menu = getMenuById(menuId);
        menu.changeStatusHidden();

    }

    @Transactional
    public void changeStatusDefault(Long menuId) {
        Menu menu = getMenuById(menuId);
        menu.changeStatusDefault();

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
                .map(menu -> {
                    String awsImagePathURL = fileService.getFilePath(menu.getImageFileName()); // AWS S3에 저장된 메뉴의 이미지 URL 을 가져옵니다.
                    return MenuResponse.toMenuResponse(menu, awsImagePathURL);
                })
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
        menuRepository.deleteAllByStatusAndUpdateDateTime24Hour(Menu.Status.DELETED, LocalDateTime.now().minusHours(24));
    }

}
