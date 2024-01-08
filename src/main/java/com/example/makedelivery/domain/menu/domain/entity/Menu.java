package com.example.makedelivery.domain.menu.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_MENU")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MENU_GROUP_ID")
    private Long menuGroupId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "IMG_FILE_NAME")
    private String imageFileName;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        HIDDEN("숨김"),
        DELETED("삭제");

        private final String description;
    }

    @Builder
    public Menu(String name, Integer price, String description, Long menuGroupId, String imageFileName,
                Status status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.menuGroupId = menuGroupId;
        this.status = status;
        this.imageFileName = imageFileName;
    }

    /**
     * 해당 메뉴가 속한 메뉴 그룹의 변경이 가능합니다.
     */
    public void updateMenuInfo(String name, String description, Integer price, Long menuGroupId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.menuGroupId = menuGroupId;
    }

    public void updateMenuImage(String imageFileName ) {
        this.imageFileName = imageFileName;
    }

    public void changeStatusHidden() {
        this.status = Status.HIDDEN;
    }

    public void changeStatusDefault() {
        this.status = Status.DEFAULT;
    }

    public void deleteMenu( ) {
        this.status = Status.DELETED;
    }

    public boolean isDeletedCheck() {
        return this.status.equals(Status.DELETED);
    }

    public boolean isHiddenCheck() {
        return this.status.equals(Status.HIDDEN);
    }

}
