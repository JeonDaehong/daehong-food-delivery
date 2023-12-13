package com.example.makedelivery.domain.menu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_MENU")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

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

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        HIDDEN("숨김"),
        DELETED("삭제");

        private final String description;
    }

    @Builder
    public Menu(String name, Integer price, String description, Long menuGroupId, String imageFileName, Integer priority,
                Status status, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.menuGroupId = menuGroupId;
        this.status = status;
        this.imageFileName = imageFileName;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    public void updateMenuInfo(String name, String description, Integer price, Long menuGroupId, String imageFileName, LocalDateTime updateDateTime) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.menuGroupId = menuGroupId;
        this.imageFileName = imageFileName;
        this.updateDateTime = updateDateTime;
    }

    public void changeStatusHidden(LocalDateTime updateDateTime) {
        this.status = Status.HIDDEN;
        this.updateDateTime = updateDateTime;
    }

    public void changeStatusDefault(LocalDateTime updateDateTime) {
        this.status = Status.DEFAULT;
        this.updateDateTime = updateDateTime;
    }

    public void deleteMenu(LocalDateTime updateDateTime) {
        this.status = Status.DELETED;
        this.updateDateTime = updateDateTime;
    }

}
