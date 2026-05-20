package com.inventory.management.product.repository;

import com.inventory.management.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface

ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(Product.Category category);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsBySkuAndIdNot(String sku, Long id);
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderLevel AND p.active = true")
    List<Product> findLowStockProducts();
    long countByActiveTrue();
}