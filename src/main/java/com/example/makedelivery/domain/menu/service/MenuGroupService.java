package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.menu.domain.MenuGroupRequest;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup.Status;
import com.example.makedelivery.domain.menu.repository.MenuGroupRepository;
import com.example.makedelivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public void addMenuGroup(MenuGroupRequest request, Long storeId) {
        MenuGroup menuGroup = MenuGroupRequest.toEntity(request, storeId);
        menuGroupRepository.save(menuGroup);
    }

    @Transactional
    public void updateMenuGroupInformation(Long menuGroupId, MenuGroupRequest request) {

        MenuGroup menuGroup = menuGroupRepository
                .findMenuGroupByIdAndStatus(menuGroupId, Status.DEFAULT)
                .orElseThrow(() -> new ApiException(MENU_GROUP_NOT_FOUND));

        menuGroup.updateMenuGroup(request.getName());
    }

    @Transactional
    public void deleteMenuGroup(Long menuGroupId) {
        // 해당 그룹에 정상적인 메뉴가 하나라도 있으면, 삭제할 수 없습니다.
        int menuCount = menuRepository.countAllByMenuGroupIdAndStatusNot(menuGroupId, Menu.Status.DELETED);
        if ( menuCount > 0 ) throw new ApiException(MENU_GROUP_DELETE);

        MenuGroup menuGroup = menuGroupRepository.findMenuGroupByIdAndStatus(menuGroupId, Status.DEFAULT)
                .orElseThrow(() -> new ApiException(MENU_GROUP_NOT_FOUND));

        menuGroup.deleteMenuGroup();
    }

    /**
     * 스케쥴러를 통해 상태가 Deleted 면서, 24시간 이상 지난 메뉴 그룹을
     * 삭제해 줍니다.
     * 상태를 변경하지 않고, 바로 삭제를 하면
     * 현재 진행중인 다른 로직에 영향을 받을 수 있기 때문에
     * 이렇게 지연 삭제처리를 해줍니다.
     */
    @Transactional
    public void deleteAllMenuGroupsDeleteStatus() {
        menuGroupRepository.deleteAllByStatusAndUpdateDateTime24Hour(Status.DELETED, LocalDateTime.now().minusHours(24));
    }
}
