package com.example.makedelivery.domain.menu.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@ToString
@Table(name = "TB_OPTN")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTN_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        HIDDEN("숨김"),
        DELETED("삭제");

        private final String description;
    }

    @Builder
    public Option(String name, Integer price, Long menuId,
                  Status status) {
        this.name = name;
        this.price = price;
        this.menuId = menuId;
        this.status = status;
    }

    /**
     * 메뉴 정보 안에서 옵션을 생성하거나 수정하므로,
     * 해당 옵션에 대한 메뉴( MENU_ID ) 는 변경 할 수 없습니다.
     */
    public void updateOptionInfo(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public void changeStatusHidden() {
        this.status = Status.HIDDEN;
    }

    public void changeStatusDefault() {
        this.status = Status.DEFAULT;
    }

    public void deleteMenu() {
        this.status = Status.DELETED;
    }

}
