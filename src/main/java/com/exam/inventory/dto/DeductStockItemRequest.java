package com.exam.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductStockItemRequest {

    @NotBlank(message = "El código de producto (productCode) es obligatorio")
    private String productCode;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad a descontar debe ser mayor a 0")
    private Integer quantity;
}