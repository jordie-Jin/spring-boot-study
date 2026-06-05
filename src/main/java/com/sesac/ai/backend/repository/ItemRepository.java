package com.sesac.ai.backend.repository;

import com.sesac.ai.backend.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContaining(String keyword);
}
