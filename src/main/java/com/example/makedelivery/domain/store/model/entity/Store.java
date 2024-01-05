package com.example.makedelivery.domain.store.model.entity;

import com.example.makedelivery.common.constants.DateEntity;
import com.example.makedelivery.domain.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_STORE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends DateEntity {

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

    @Column(name = "IMG_FILE_NAME")
    private String imageFileName;

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
                 String introduction, String imageFileName, Long categoryId) {
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
        this.imageFileName = imageFileName;
    }

    public void openStore() {
        this.openStatus = OpenStatus.OPENED;
    }

    public void closeStore() {
        this.openStatus = OpenStatus.CLOSED;
    }

    /**
     * 매장 정보 변경 : 업종은 변경이 불가능합니다.
     */
    public void updateStoreInfo(String name, String phone, String address, Double longitude, Double latitude,
                                String introduction) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.introduction = introduction;
    }

    public void updateStoreImage(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public void deleteStoreStatus() {
        this.status = Status.DELETED;
    }

}
