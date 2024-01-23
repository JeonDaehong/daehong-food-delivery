package com.example.makedelivery.domain.store.model;

import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.OpenStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@ToString
public class StoreResponse {

    private Long id;
    private String name;
    private String phone;
    private String address;
    private Long ownerId;
    private OpenStatus openStatus;
    private String introduction;
    private Long categoryId;
    private Double longitude;
    private Double latitude;
    private String awsImagePathURL;


    public static StoreResponse toStoreResponse(Store store, String awsImagePathURL) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .phone(store.getPhone())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .ownerId(store.getOwnerId())
                .openStatus(store.getOpenStatus())
                .introduction(store.getIntroduction())
                .categoryId(store.getCategoryId())
                .awsImagePathURL(awsImagePathURL)
                .build();
    }

}
