package com.example.makedelivery.domain.store.model.entity;

import com.example.makedelivery.domain.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_STORE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID")
    private Long id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "LONGITUDE") // 경도 x축
    private Double longitude;

    @Column(name = "LATITUDE") // 위도 y축
    private Double latitude;

    @Column(name = "MEMBER_ID")
    private Long ownerId;

    @Column(name = "OPEN_STATUS")
    @Enumerated(value = EnumType.STRING)
    private OpenStatus openStatus;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "INTRO")
    private String introduction;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;

    @Getter
    @RequiredArgsConstructor
    public enum OpenStatus {
        OPENED("개점"),
        CLOSED("폐점");

        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        DELETED("삭제");

        private final String description;
    }

    @Builder
    public Store(String name, String phone, String address, Double longitude, Double latitude, Long ownerId, OpenStatus openStatus, Status status,
                 String introduction, Long categoryId, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ownerId = ownerId;
        this.openStatus = openStatus;
        this.status = status;
        this.introduction = introduction;
        this.categoryId = categoryId;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    public void openStore(LocalDateTime updateDateTime) {
        this.openStatus = OpenStatus.OPENED;
        this.updateDateTime = updateDateTime;
    }

    public void closeStore(LocalDateTime updateDateTime) {
        this.openStatus = OpenStatus.CLOSED;
        this.updateDateTime = updateDateTime;
    }

    /**
     * 매장 정보 변경 : 업종은 변경이 불가능합니다.
     */
    public void updateStoreInfo(String name, String phone, String address, Double longitude, Double latitude,
                                String introduction, LocalDateTime updateDateTime) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.introduction = introduction;
        this.updateDateTime = updateDateTime;
    }

    public void deleteStoreStatus(LocalDateTime updateDateTime) {
        this.status = Status.DELETED;
        this.updateDateTime = updateDateTime;
    }

}
