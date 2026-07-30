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
@Tag(name = "Inventory Controller", description = "API para la verificación y gestión de stock por código de negocio")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/deduct")
    @Operation(summary = "Descontar stock en lote (Batch)", description = "Verifica y descuenta el stock de una lista de productos identificados por productCode de forma atómica")
    @ApiResponse(responseCode = "200", description = "Retorna true si el stock fue descontado, false si no hay suficiente o el producto no existe")
    public ResponseEntity<Boolean> deductStock(@Valid @RequestBody DeductStockRequest request) {
        boolean result = inventoryService.deductStock(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Cargar o incrementar stock de un producto usando productCode")
    @ApiResponse(responseCode = "200", description = "Inventario creado o actualizado exitosamente")
    public ResponseEntity<InventoryResponse> addOrUpdateStock(@Valid @RequestBody UpdateStockRequest request) {
        InventoryResponse response = inventoryService.addOrUpdateStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productCode}")
    @Operation(summary = "Consultar stock disponible por código de negocio (productCode)")
    @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<InventoryResponse> getStockByProductCode(@PathVariable String productCode) {
        InventoryResponse response = inventoryService.getStockByProductCode(productCode);
        return ResponseEntity.ok(response);
    }
}