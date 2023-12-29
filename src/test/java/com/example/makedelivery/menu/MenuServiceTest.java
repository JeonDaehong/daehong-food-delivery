package com.example.makedelivery.menu;

import com.example.makedelivery.domain.menu.domain.MenuRequest;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.repository.MenuRepository;
import com.example.makedelivery.domain.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    private MenuRequest menuRequest;

    @BeforeEach
    void setTestMenu() {
        menuRequest = MenuRequest.builder()
                .name("페퍼로니 피자")
                .price(12_000)
                .description("아주 맛있는 피자입니다.")
                .menuGroupId(1L)
                .build();
    }

    @Test
    @DisplayName("새로운 메뉴를 추가할 수 있습니다.")
    void addMenuTest() {
        menuService.addMenu(menuRequest, "testFileName.png");
        verify(menuRepository, times(1)).save(any(Menu.class));
    }



}
