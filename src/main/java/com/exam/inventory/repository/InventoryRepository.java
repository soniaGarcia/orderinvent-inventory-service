package com.exam.inventory.repository;

import com.exam.inventory.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, Long> {
    
    Optional<ProductInventory> findByProductCode(String productCode);
}