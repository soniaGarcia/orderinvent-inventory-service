package com.exam.inventory.service;

import com.exam.inventory.dto.DeductStockRequest;
import com.exam.inventory.dto.InventoryResponse;
import com.exam.inventory.dto.UpdateStockRequest;
import com.exam.inventory.exception.ResourceNotFoundException;
import com.exam.inventory.model.ProductInventory;
import com.exam.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public boolean deductStock(DeductStockRequest request) {
        log.info("Procesando descuento de stock para el producto: {} (Cantidad: {})", 
                request.getProductId(), request.getQuantity());

        return inventoryRepository.findByProductId(request.getProductId())
                .map(inventory -> {
                    if (inventory.getAvailableStock() >= request.getQuantity()) {
                        inventory.setAvailableStock(inventory.getAvailableStock() - request.getQuantity());
                        inventoryRepository.save(inventory);
                        log.info("Stock descontado exitosamente. Producto: {}, Stock restante: {}", 
                                request.getProductId(), inventory.getAvailableStock());
                        return true;
                    }
                    log.warn("Stock insuficiente para el producto: {}. Disponible: {}, Solicitado: {}", 
                            request.getProductId(), inventory.getAvailableStock(), request.getQuantity());
                    return false;
                })
                .orElseGet(() -> {
                    log.error("No se encontró el producto en el inventario: {}", request.getProductId());
                    return false;
                });
    }

    @Transactional
    public InventoryResponse addOrUpdateStock(UpdateStockRequest request) {
        log.info("Actualizando inventario para el producto: {} con stock: {}", request.getProductId(), request.getStock());

        ProductInventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .map(existing -> {
                    existing.setAvailableStock(existing.getAvailableStock() + request.getStock());
                    return existing;
                })
                .orElseGet(() -> ProductInventory.builder()
                        .productId(request.getProductId())
                        .availableStock(request.getStock())
                        .build());

        inventory = inventoryRepository.save(inventory);
        return mapToResponse(inventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStockByProductId(String productId) {
        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en inventario: " + productId));
        return mapToResponse(inventory);
    }

    private InventoryResponse mapToResponse(ProductInventory inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .availableStock(inventory.getAvailableStock())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}