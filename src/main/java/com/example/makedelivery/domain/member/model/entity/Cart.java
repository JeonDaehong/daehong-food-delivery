package com.example.makedelivery.domain.member.model.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "TB_CART")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long id;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "MENU_NM")
    private String menuName;

    @Column(name = "STORE_ID")
    private Long storeId;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "COUNT")
    private Integer count;

    @Builder
    public Cart(Integer price, Long menuId, Long storeId, Long memberId, String menuName, Integer count) {
        this.price = price;
        this.menuId = menuId;
        this.menuName = menuName;
        this.storeId = storeId;
        this.memberId = memberId;
        this.count = count;
    }

}
