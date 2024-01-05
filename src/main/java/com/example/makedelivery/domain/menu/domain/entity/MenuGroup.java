package com.example.makedelivery.domain.menu.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import com.example.makedelivery.domain.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_MENU_GROUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuGroup extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_GROUP_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STORE_ID")
    private Long storeId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        DELETED("삭제");

        private final String description;
    }


    @Builder
    public MenuGroup(String name, Long storeId, Status status) {
        this.name = name;
        this.storeId = storeId;
        this.status = status;
    }

    public void updateMenuGroup(String name) {
        this.name = name;
    }

    public void deleteMenuGroup() {
        this.status = Status.DELETED;
    }

}
