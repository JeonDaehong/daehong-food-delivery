package com.example.makedelivery.domain.menu.repository;

import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.domain.entity.Option.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<Option> findOptionByIdAndMenuId(Long id, Long menuId);

    Optional<List<Option>> findAllByMenuIdAndStatusOrderByName(Long menuId, Status status);

    void deleteAllByStatus(Status status);
    
}
