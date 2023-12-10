package com.example.makedelivery.domain.member.model.entity;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "EMAIL", unique = true) // 중복을 허용하지 않도록 설정
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NICKNAME")
    private String nickname;

    @Column(name = "LEVEL")
    @Enumerated(value = EnumType.STRING)
    private MemberLevel memberLevel;

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;


    @Builder
    public Member(String email, String password, String nickname, MemberLevel memberLevel, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberLevel = memberLevel;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
