package com.exam.inventory.service;

import com.exam.inventory.dto.DeductStockItemRequest;
import com.exam.inventory.dto.DeductStockRequest;
import com.exam.inventory.model.ProductInventory;
import com.exam.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Debe descontar el stock si todos los productos tienen existencias suficientes")
    void deductStock_Success_AllItemsAvailable() {
        // Arrange
        ProductInventory prodA = ProductInventory.builder().productCode("PROD-A100").availableStock(10).build();
        ProductInventory prodB = ProductInventory.builder().productCode("PROD-B200").availableStock(5).build();

        when(inventoryRepository.findByProductCode("PROD-A100")).thenReturn(Optional.of(prodA));
        when(inventoryRepository.findByProductCode("PROD-B200")).thenReturn(Optional.of(prodB));

        DeductStockRequest request = DeductStockRequest.builder()
                .items(List.of(
                        new DeductStockItemRequest("PROD-A100", 2),
                        new DeductStockItemRequest("PROD-B200", 1)
                ))
                .build();

        // Act
        boolean result = inventoryService.deductStock(request);

        // Assert
        assertTrue(result);
        assertEquals(8, prodA.getAvailableStock());
        assertEquals(4, prodB.getAvailableStock());
        verify(inventoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe hacer Rollback (retornar false) si un solo producto no tiene stock suficiente")
    void deductStock_Rollback_WhenOneItemLacksStock() {
        // Arrange
        ProductInventory prodA = ProductInventory.builder().productCode("PROD-A100").availableStock(10).build();
        ProductInventory prodB = ProductInventory.builder().productCode("PROD-B200").availableStock(0).build();

        when(inventoryRepository.findByProductCode("PROD-A100")).thenReturn(Optional.of(prodA));
        when(inventoryRepository.findByProductCode("PROD-B200")).thenReturn(Optional.of(prodB));

        DeductStockRequest request = DeductStockRequest.builder()
                .items(List.of(
                        new DeductStockItemRequest("PROD-A100", 2),
                        new DeductStockItemRequest("PROD-B200", 1) // Falla aquí
                ))
                .build();

        // Act
        boolean result = inventoryService.deductStock(request);

        // Assert
        assertFalse(result);
        verify(inventoryRepository, never()).saveAll(anyList());
    }
}