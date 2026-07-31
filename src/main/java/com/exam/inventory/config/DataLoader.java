package com.exam.inventory.config;

import com.exam.inventory.model.ProductInventory;
import com.exam.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) {
        if (inventoryRepository.count() == 0) {
            log.info("Cargando datos iniciales de stock en la base de datos H2...");
            
            inventoryRepository.save(ProductInventory.builder()
                    .productCode("PROD-001")
                    .availableStock(100)
                    .build());
                    
            inventoryRepository.save(ProductInventory.builder()
                    .productCode("PROD-002")
                    .availableStock(50)
                    .build());

            log.info("Stock inicial cargado con éxito.");
        }
    }
}