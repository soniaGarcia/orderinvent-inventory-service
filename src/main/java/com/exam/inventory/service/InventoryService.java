package com.exam.inventory.service;

import com.exam.inventory.dto.*;
import com.exam.inventory.exception.ResourceNotFoundException;
import com.exam.inventory.model.ProductInventory;
import com.exam.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public boolean deductStock(DeductStockRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("Solicitud de descuento de stock vacía.");
            return false;
        }

        List<ProductInventory> inventoriesToUpdate = new ArrayList<>();

        // FASE 1: Validación Atómica por productCode
        for (DeductStockItemRequest itemReq : request.getItems()) {
            log.info("Verificando stock para el código de producto: {} (Cantidad: {})", 
                    itemReq.getProductCode(), itemReq.getQuantity());

            ProductInventory inventory = inventoryRepository.findByProductCode(itemReq.getProductCode())
                    .orElse(null);

            if (inventory == null) {
                log.error("Producto no encontrado en inventario con código: {}", itemReq.getProductCode());
                return false; // Rollback
            }

            if (inventory.getAvailableStock() < itemReq.getQuantity()) {
                log.warn("Stock insuficiente para el producto {}. Disponible: {}, Solicitado: {}", 
                        itemReq.getProductCode(), inventory.getAvailableStock(), itemReq.getQuantity());
                return false; // Rollback
            }

            inventory.setAvailableStock(inventory.getAvailableStock() - itemReq.getQuantity());
            inventoriesToUpdate.add(inventory);
        }

        // FASE 2: Persistencia Batch
        inventoryRepository.saveAll(inventoriesToUpdate);
        log.info("Stock descontado exitosamente para {} productos.", inventoriesToUpdate.size());
        
        return true;
    }

    @Transactional
    public InventoryResponse addOrUpdateStock(UpdateStockRequest request) {
        log.info("Actualizando inventario para el código de producto: {} con stock: {}", 
                request.getProductCode(), request.getStock());

        ProductInventory inventory = inventoryRepository.findByProductCode(request.getProductCode())
                .map(existing -> {
                    existing.setAvailableStock(existing.getAvailableStock() + request.getStock());
                    return existing;
                })
                .orElseGet(() -> ProductInventory.builder()
                        .productCode(request.getProductCode())
                        .availableStock(request.getStock())
                        .build());

        inventory = inventoryRepository.save(inventory);
        return mapToResponse(inventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStockByProductCode(String productCode) {
        ProductInventory inventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + productCode));
        return mapToResponse(inventory);
    }

    private InventoryResponse mapToResponse(ProductInventory inventory) {
        return InventoryResponse.builder()
                .productCode(inventory.getProductCode())
                .availableStock(inventory.getAvailableStock())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}