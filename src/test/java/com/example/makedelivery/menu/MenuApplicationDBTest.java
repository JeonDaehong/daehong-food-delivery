package com.example.makedelivery.menu;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.menu.domain.*;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.repository.MenuGroupRepository;
import com.example.makedelivery.domain.menu.repository.MenuRepository;
import com.example.makedelivery.domain.menu.service.MenuApplicationService;
import com.example.makedelivery.domain.menu.service.MenuGroupService;
import com.example.makedelivery.domain.menu.service.MenuService;
import com.example.makedelivery.domain.menu.service.OptionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;


@SpringBootTest
public class MenuApplicationDBTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuApplicationService menuApplicationService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private Member owner;

    @BeforeEach
    void setMember() {
        owner = memberService.findMemberByEmail("bTestAdmin710a@admin.co.kr");
    }

    @Test
    @DisplayName("매장 점주는 본인 매장에서 메뉴 그룹을 생성 할 수 있습니다.")
    @Transactional
    void addMenuGroupTest() {
        // given
        MenuGroupRequest request = MenuGroupRequest.builder()
                .name("양념 치킨 그룹")
                .build();
        MenuGroup menuGroup = MenuGroupRequest.toEntity(request, 1L);
        // when
        Long menuGroupId = menuGroupRepository.save(menuGroup).getId();
        MenuGroup dbMenuGroup = menuGroupRepository.findById(menuGroupId).orElse(null);
        // then
        assertThat(dbMenuGroup).isNotNull();
        assertThat(menuGroupId).isEqualTo(dbMenuGroup.getId());
    }

    @Test
    @DisplayName("매장 점주는 본인 매장에서 메뉴를 생성 할 수 있습니다. - AWS S3 Test 는 완료되었기 때문에, Menu Add 만 테스트합니다.")
    @Transactional
    void addMenuTest() {
        // given
        MenuRequest request = MenuRequest.builder()
                .name("맛있는 치킨")
                .price(20_000)
                .description("맛있는 치킨입니다.")
                .menuGroupId(1L)
                .build();
        Menu menu = MenuRequest.toEntity(request, "TestMenuImage.png");
        // when
        Long menuId = menuRepository.save(menu).getId();
        Menu dbMenu = menuService.getMenuById(menuId);
        // then
        assertThat(dbMenu).isNotNull();
        assertThat(menuId).isEqualTo(dbMenu.getId());
    }

    @Test
    @DisplayName("매장 점주는 본인 매장의 메뉴 상태를 변경할 수 있다.")
    void changeMenuStatusTest() {
        // hidden when
        menuApplicationService.changeStatusHidden(1L, 1L, owner);
        Menu dbMenu = menuService.getMenuById(1L);
        // hidden then
        assertThat(Menu.Status.HIDDEN).isEqualTo(dbMenu.getStatus());
        // default when
        menuApplicationService.changeStatusDefault(1L, 1L, owner);
        dbMenu = menuService.getMenuById(1L);
        // default then
        assertThat(Menu.Status.DEFAULT).isEqualTo(dbMenu.getStatus());
    }

    @Test
    @DisplayName("해당 매장에 있는 메뉴그룹과, 메뉴를 전부 가져옵니다 ( 메뉴는 이름 순 ). - Test DB로 테스트 진행 ")
    @Transactional
    void getMenuListTest() {
        // given
        MenuGroupRequest menuGroupRequest = MenuGroupRequest.builder()
                .name("양념 치킨 그룹")
                .build();
        MenuGroup menuGroup = MenuGroupRequest.toEntity(menuGroupRequest, 1L);
        MenuRequest menuRequest = MenuRequest.builder()
                .name("맛있는 치킨")
                .price(20_000)
                .description("맛있는 치킨입니다.")
                .menuGroupId(1L)
                .build();
        Menu menu = MenuRequest.toEntity(menuRequest, "TestMenuImage.png");
        // when
        menuGroupRepository.save(menuGroup);
        menuRepository.save(menu);
        // then ( System.out.println )
        menuService.getMenuList(1L)
                .forEach(menuGroupResponse -> {
                    System.out.println("[그룹] : " + menuGroupResponse.toString());
                    menuGroupResponse.getMenuList()
                            .forEach(menuResponse ->
                            System.out.println("[메뉴] : " + menuResponse.toString())
                    );
                    System.out.println();
                });
    }

    @Test
    @DisplayName("점주는 본인 매장의 메뉴를 수정할 수 있습니다.")
    @Transactional
    void updateMenuInformationTest() {
        // given
        final String UPDATE_NAME = "이름 바꾼 치킨";
        MenuRequest request = MenuRequest.builder()
                .name(UPDATE_NAME)
                .price(20_000)
                .description("맛있는 치킨입니다.")
                .menuGroupId(1L)
                .build();
        // when
        menuService.updateMenuInformation(1L, request);
        Menu dbMenu = menuService.getMenuById(1L);
        // then
        assertThat(UPDATE_NAME).isEqualTo(dbMenu.getName());
    }

    @Test
    @DisplayName("해당 메뉴에 대하여 옵션을 추가할 수 있고, 조회 할 수 있습니다.")
    @Transactional
    void addMenuOptionTest() {
        OptionRequest option1 = OptionRequest.builder()
                .name("테스트 옵션 1")
                .price(2_000)
                .build();
        OptionRequest option2 = OptionRequest.builder()
                .name("테스트 옵션 2")
                .price(3_000)
                .build();
        optionService.addOption(option1, 1L);
        optionService.addOption(option2, 1L);
        List<OptionResponse> optionList = optionService.getMenuOptions(1L);
        for ( OptionResponse option : optionList ) {
            System.out.println("Option : " + option.toString());
        }
    }

    @Test
    @DisplayName("옵션 정보를 수정할 수 있습니다.")
    @Transactional
    void updateMenuOptionTest() {
        // given
        final String UPDATE_NAME = "바뀐 옵션";
        OptionRequest request = OptionRequest.builder()
                .name(UPDATE_NAME)
                .price(2_000)
                .build();
        // when
        optionService.updateOption(request, 1L, 1L);
        Option dbOption = optionService.findOptionById(1L);
        // then
        assertThat(UPDATE_NAME).isEqualTo(dbOption.getName());
    }

}
