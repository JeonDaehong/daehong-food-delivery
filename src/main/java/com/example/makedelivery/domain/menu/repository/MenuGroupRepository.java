package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.menu.domain.entity.MenuGroup;
import com.example.makedelivery.domain.menu.domain.entity.MenuGroup.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {

    Optional<List<MenuGroup>> findAllByStoreIdAndStatusOrderByName(Long storeId, Status status);

    Optional<MenuGroup> findMenuGroupByIdAndStatus(Long id, Status status);

    void deleteAllByStatus(Status status);

}
