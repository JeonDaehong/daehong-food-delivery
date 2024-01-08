package com.example.makedelivery.domain.order.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Table(name = "TB_ORDER_MENU")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_MENU_ID")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "MENU_PRICE")
    private Integer price;

    @Column(name = "MENU_COUNT")
    private Integer count;

    @Builder
    public OrderMenu(Long orderId, Long menuId, Integer price, Integer count) {
        this.orderId = orderId;
        this.menuId = menuId;
        this.price = price;
        this.count = count;
    }

}
