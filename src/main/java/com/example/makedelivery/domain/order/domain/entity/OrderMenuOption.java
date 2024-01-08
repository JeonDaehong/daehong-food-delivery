package com.example.makedelivery.domain.order.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Table(name = "TB_ORDER_OPTN")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOption extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_OPTN_ID")
    private Long id;

    @Column(name = "ORDER_MENU_ID")
    private Long orderMenuId;

    @Column(name = "OPTN_ID")
    private Long optionId;

    @Column(name = "OPTN_PRICE")
    private Integer price;

    @Builder
    public OrderMenuOption(Long orderMenuId, Long optionId, Integer price) {
        this.orderMenuId = orderMenuId;
        this.optionId = optionId;
        this.price = price;
    }

}
