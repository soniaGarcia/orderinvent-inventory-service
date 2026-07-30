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
public class UpdateStockRequest {

    @NotBlank(message = "El código de producto (productCode) es obligatorio")
    private String productCode;

    @NotNull(message = "El stock inicial/adicional es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}