package com.exam.inventory.controller;

import com.exam.inventory.dto.DeductStockRequest;
import com.exam.inventory.dto.InventoryResponse;
import com.exam.inventory.dto.UpdateStockRequest;
import com.exam.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Controller", description = "API para la verificación y gestión de stock de productos")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/deduct")
    @Operation(summary = "Descontar stock de un producto", description = "Invocado por Order Service para confirmar disponibilidad y reducir existencias de forma transaccional")
    @ApiResponse(responseCode = "200", description = "Retorna true si el stock fue descontado, false si es insuficiente o no existe")
    public ResponseEntity<Boolean> deductStock(@Valid @RequestBody DeductStockRequest request) {
        boolean result = inventoryService.deductStock(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Cargar o incrementar stock de un producto")
    @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente")
    public ResponseEntity<InventoryResponse> addOrUpdateStock(@Valid @RequestBody UpdateStockRequest request) {
        InventoryResponse response = inventoryService.addOrUpdateStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Consultar stock disponible de un producto")
    @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<InventoryResponse> getStockByProductId(@PathVariable String productId) {
        InventoryResponse response = inventoryService.getStockByProductId(productId);
        return ResponseEntity.ok(response);
    }
}