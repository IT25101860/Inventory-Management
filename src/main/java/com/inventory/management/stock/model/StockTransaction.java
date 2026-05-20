package com.inventory.management.stock.model;

import com.inventory.management.common.model.BaseEntity;
import com.inventory.management.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "stock_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class StockTransaction extends BaseEntity {

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @Size(max = 300)
    @Column(length = 300)
    private String notes;

    public enum TransactionType { STOCK_IN, STOCK_OUT, ADJUSTMENT }
}