package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Menu.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {


    Optional<List<Menu>> findAllByMenuGroupIdAndStatusOrderByName(Long menuGroupId, Status status);


    int countAllByMenuGroupIdAndStatusNot(long menuGroupId, Status status);

    Optional<Menu> findMenuById(Long id);

    @Modifying
    @Query("DELETE FROM Menu e WHERE e.status = :status AND e.updateDateTime <= :cutOffDateTime")
    void deleteAllByStatusAndUpdateDateTime24Hour(@Param("status") Status status,
                                                  @Param("cutOffDateTime") LocalDateTime cutOffDateTime);

}
