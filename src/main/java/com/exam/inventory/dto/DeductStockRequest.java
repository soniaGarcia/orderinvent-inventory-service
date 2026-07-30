package com.exam.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductStockRequest {

    @NotEmpty(message = "La lista de productos no puede estar vacía")
    @Valid
    private List<DeductStockItemRequest> items;
}