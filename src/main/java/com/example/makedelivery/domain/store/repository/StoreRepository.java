package com.example.makedelivery.domain.store.repository;

import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByName(String name);

    Optional<List<Store>> findAllByOwnerIdAndStatusOrderByName(Long ownerId, Status status);

    Optional<Store> findByIdAndOwnerIdAndStatus(Long id, Long ownerId, Status status);

    Optional<Store> findStoreById(Long id);

    Optional<Store> findStoreByName(String name);

    boolean existsByIdAndOwnerIdAndStatus(Long id, Long ownerId, Status status);

    void deleteAllByStatus(Status status);

    /**
     * 메인 주소를 입력한 회원이 StoreList 를 검색하였을 경우 해당 쿼리문을 사용합니다.
     * <br><br>
     * 위도와 경도를 가지고 Haversine 공식을 이용하여, 거리를 계산합니다.
     * 해당 쿼리문은 유저의 메인 주소의 위도 경도와, 매장의 위도 경도를 토대로 직선 거리를 구합니다.
     * 그리고 10KM 이내의 매장만 검색해주며, 가까운 순서로 반환합니다. ( 해당 카테고리 ID를 가진 매장 중에서 )
     */
    @Query("SELECT s FROM Store s " +
            "WHERE " +
            " 6371 * acos(" +
            "   cos(radians(:userLatitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:userLongitude)) + " +
            "   sin(radians(:userLatitude)) * sin(radians(s.latitude))" +
            " ) <= 10 AND s.categoryId = :categoryId " +
            "ORDER BY " +
            " 6371 * acos(" +
            "   cos(radians(:userLatitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:userLongitude)) + " +
            "   sin(radians(:userLatitude)) * sin(radians(s.latitude))" +
            " )")
    Optional<List<Store>> findAllWithInDistanceInCategoryIdOrderByDistance(@Param("userLatitude") Double userLatitude,
                                                                           @Param("userLongitude") Double userLongitude,
                                                                           @Param("categoryId") Long categoryId);

    /**
     * 비회원이거나, 메인 주소를 입력하지 않은 회원이 StoreList 를 조회하였을 경우 해당 쿼리문을 사용합니다.
     * 해당 카테고리에 포함된 매장들을 이름 순서로 30개를 반환합니다.
     */
    Optional<List<Store>> findTop30ByCategoryIdOrderByName(Long categoryId);

    @Query("SELECT s FROM Store s WHERE s.name LIKE %:keyword% ORDER BY s.name")
    Optional<List<Store>> findAllByNameContainingKeywordOrderByName(String keyword);

    @Query("SELECT s FROM Store s " +
            "WHERE " +
            " 6371 * acos(" +
            "   cos(radians(:userLatitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:userLongitude)) + " +
            "   sin(radians(:userLatitude)) * sin(radians(s.latitude))" +
            " ) <= 10 " +
            "ORDER BY " +
            " 6371 * acos(" +
            "   cos(radians(:userLatitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:userLongitude)) + " +
            "   sin(radians(:userLatitude)) * sin(radians(s.latitude))" +
            " )")
    Optional<List<Store>> findAllWithInDistanceOrderByDistance(@Param("userLatitude") Double userLatitude,
                                                               @Param("userLongitude") Double userLongitude);

}
