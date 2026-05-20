package com.inventory.management.product.service;

import com.inventory.management.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {


    Product saveProductWithImage(Product product, org.springframework.web.multipart.MultipartFile imageFile) throws java.io.IOException;
    Product updateProductWithImage(Long id, Product product, org.springframework.web.multipart.MultipartFile imageFile) throws java.io.IOException;

    List<Product> getAllActiveProducts();
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product saveProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> searchProducts(String keyword);
    List<Product> getLowStockProducts();
    boolean isSkuTaken(String sku, Long excludeId);
    long countProducts();
}