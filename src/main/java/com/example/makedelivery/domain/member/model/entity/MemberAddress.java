package com.example.makedelivery.domain.member.model.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_MEMBER_ADDRESS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddress extends DateEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "LONGITUDE") // 경도 x축
    private Double longitude;

    @Column(name = "LATITUDE") // 위도 y축
    private Double latitude;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        DELETED("삭제");

        private final String description;
    }

    @Builder
    public MemberAddress(String address, Double longitude, Double latitude, Long memberId,
                         Status status, Integer priority) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.memberId = memberId;
        this.status = status;
        this.priority = priority;
    }

    public void changeAddressPriority(Integer priority) {
        this.priority = priority;
    }

    public void deleteAddress() {
        this.status = Status.DELETED;
    }

}
