package com.example.makedelivery.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "TB_CART")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

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

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;

    @Builder
    public Cart(Integer price, Long menuId, Long storeId, Long memberId, String menuName, Integer count,
                LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.price = price;
        this.menuId = menuId;
        this.menuName = menuName;
        this.storeId = storeId;
        this.memberId = memberId;
        this.count = count;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

}
