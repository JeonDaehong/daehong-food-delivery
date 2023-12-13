package com.example.makedelivery.domain.store.repository;

import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByName(String name);

    Optional<List<Store>> findAllByOwnerIdAndStatusOrderByName(Long ownerId, Status status);

    Optional<Store> findByIdAndOwnerIdAndStatus(Long Id, Long ownerId, Status status);

    boolean existsByIdAndOwnerIdAndStatus(Long id, Long ownerId, Status status);
}
