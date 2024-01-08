package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {

    Optional<List<MenuGroup>> findAllByStoreIdAndStatusOrderByName(Long storeId, Status status);

    Optional<MenuGroup> findMenuGroupByIdAndStatus(Long id, Status status);

    @Modifying
    @Query("DELETE FROM MenuGroup e WHERE e.status = :status AND e.updateDateTime <= :cutOffDateTime")
    void deleteAllByStatusAndUpdateDateTime24Hour(@Param("status") Status status,
                                                  @Param("cutOffDateTime") LocalDateTime cutOffDateTime);

}
