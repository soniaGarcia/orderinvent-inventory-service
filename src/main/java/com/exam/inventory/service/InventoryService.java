package com.exam.inventory.service;

import com.exam.inventory.dto.DeductStockItemRequest;
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

        // FASE 1: Validación Atómica (Verificar que TODOS los productos existan y tengan stock suficiente)
        for (DeductStockItemRequest itemReq : request.getItems()) {
            log.info("Verificando stock para el producto: {} (Cantidad solicitada: {})", 
                    itemReq.getProductCode(), itemReq.getQuantity());

            ProductInventory inventory = inventoryRepository.findByProductId(itemReq.getProductCode())
                    .orElse(null);

            if (inventory == null) {
                log.error("Producto no encontrado en inventario: {}", itemReq.getProductCode());
                return false; // Cancela la transacción completa
            }

            if (inventory.getAvailableStock() < itemReq.getQuantity()) {
                log.warn("Stock insuficiente para el producto: {}. Disponible: {}, Solicitado: {}", 
                        itemReq.getProductCode(), inventory.getAvailableStock(), itemReq.getQuantity());
                return false; // Cancela la transacción completa
            }

            // Preparar el ítem descontado en memoria
            inventory.setAvailableStock(inventory.getAvailableStock() - itemReq.getQuantity());
            inventoriesToUpdate.add(inventory);
        }

        // FASE 2: Persistencia en Lote (Solo se ejecuta si todos los productos pasaron la validación)
        inventoryRepository.saveAll(inventoriesToUpdate);
        log.info("Stock descontado exitosamente para {} productos.", inventoriesToUpdate.size());
        
        return true;
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