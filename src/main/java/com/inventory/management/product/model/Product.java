package com.inventory.management.product.model;

import com.inventory.management.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Product extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotBlank(message = "SKU is required")
    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Min(0)
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Min(0)
    @Column(nullable = false)
    private Integer reorderLevel = 10;

    @Column(nullable = false)
    private Boolean active = true;

    @Column
    private String imagePath; // stores filename like "product_123.jpg"

    public enum Category {
        ELECTRONICS, CLOTHING, FOOD_BEVERAGE,
        FURNITURE, STATIONERY, TOOLS, OTHER
    }

    public boolean isLowStock() { return stockQuantity <= reorderLevel; }
    public boolean isOutOfStock() { return stockQuantity == 0; }
}