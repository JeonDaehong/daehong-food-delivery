package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.domain.entity.Option.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<Option> findOptionByIdAndMenuId(Long id, Long menuId);

    Optional<Option> findOptionById(Long id);

    Optional<List<Option>> findAllByMenuIdAndStatusOrderByName(Long menuId, Status status);

    @Modifying
    @Query("DELETE FROM Option e WHERE e.status = :status AND e.updateDateTime <= :cutOffDateTime")
    void deleteAllByStatusAndUpdateDateTime24Hour(@Param("status") Status status,
                                                  @Param("cutOffDateTime") LocalDateTime cutOffDateTime);
    
}
