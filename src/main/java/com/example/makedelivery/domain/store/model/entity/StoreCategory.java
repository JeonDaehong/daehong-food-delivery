package com.example.makedelivery.domain.store.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "TB_STORE_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CRTE_DTTM")
    private LocalDateTime createDateTime;

    @Column(name = "UPDT_DTTM")
    private LocalDateTime updateDateTime;

}
