package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Menu.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {


    Optional<List<Menu>> findAllByMenuGroupIdAndStatusOrderByName(Long menuGroupId, Status status);


    int countAllByMenuGroupIdAndStatusNot(long menuGroupId, Status status);

    Optional<Menu> findMenuById(Long id);

    void deleteAllByStatus(Status status);

}
