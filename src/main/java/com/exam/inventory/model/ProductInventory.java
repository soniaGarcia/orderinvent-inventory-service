package com.exam.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory", indexes = {
    @Index(name = "idx_product_code", columnList = "productCode", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Autogenerado por la BD (Surrogate Key)

    @Column(nullable = false, unique = true)
    private String productCode; // Código de negocio / SKU (Natural Key)

    @Column(nullable = false)
    private Integer availableStock;

    @Version // Control de concurrencia optimista
    private Long version;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}