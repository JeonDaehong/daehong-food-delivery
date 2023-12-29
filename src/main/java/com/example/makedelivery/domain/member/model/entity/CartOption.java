package com.example.makedelivery.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_CART_OPTN")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_OPTN_ID")
    private Long id;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "CART_ID")
    private Long cartId;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "MENU_OPTN_ID")
    private Long menuOptionId;

    @Column(name = "MENU_OPTN_NM")
    private String menuOptionName;

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;

    @Builder
    public CartOption(Integer price, Long cartId, Long memberId, Long menuOptionId, String menuOptionName,
                      LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.price = price;
        this.cartId = cartId;
        this.memberId = memberId;
        this.menuOptionId = menuOptionId;
        this.menuOptionName = menuOptionName;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

}
