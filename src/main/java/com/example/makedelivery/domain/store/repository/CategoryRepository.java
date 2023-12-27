package com.example.makedelivery.domain.store.repository;

import com.example.makedelivery.domain.store.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
