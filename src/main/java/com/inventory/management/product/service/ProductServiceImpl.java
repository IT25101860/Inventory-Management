package com.inventory.management.product.service;

import com.inventory.management.product.model.Product;
import com.inventory.management.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override public List<Product> getAllActiveProducts() { return productRepository.findByActiveTrue(); }
    @Override public List<Product> getAllProducts() { return productRepository.findAll(); }
    @Override public Optional<Product> getProductById(Long id) { return productRepository.findById(id); }

    @Override
    public Product saveProduct(Product product) {
        if (productRepository.findBySku(product.getSku()).isPresent())
            throw new IllegalArgumentException("SKU already exists.");
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product updated) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (isSkuTaken(updated.getSku(), id))
            throw new IllegalArgumentException("SKU already used.");
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setSku(updated.getSku());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setReorderLevel(updated.getReorderLevel());
        existing.setActive(updated.getActive());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        p.setActive(false);
        productRepository.save(p);
    }

    @Override public List<Product> searchProducts(String k) { return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(k); }
    @Override public List<Product> getLowStockProducts() { return productRepository.findLowStockProducts(); }
    @Override public boolean isSkuTaken(String sku, Long excludeId) {
        if (excludeId == null) return productRepository.findBySku(sku).isPresent();
        return productRepository.existsBySkuAndIdNot(sku, excludeId);
    }
    @Override
    public long countProducts() { return productRepository.countByActiveTrue(); }

    private static final String UPLOAD_DIR = "uploads/products/";

    @Override
    public Product saveProductWithImage(Product product, MultipartFile imageFile) throws IOException {
        if (productRepository.findBySku(product.getSku()).isPresent())
            throw new IllegalArgumentException("SKU already exists.");

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = saveImage(imageFile);
            product.setImagePath(filename);
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProductWithImage(Long id, Product updated, MultipartFile imageFile) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (isSkuTaken(updated.getSku(), id))
            throw new IllegalArgumentException("SKU already used.");

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setSku(updated.getSku());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setReorderLevel(updated.getReorderLevel());
        existing.setActive(updated.getActive());

        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if exists
            if (existing.getImagePath() != null) {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + existing.getImagePath()));
            }
            existing.setImagePath(saveImage(imageFile));
        }
        return productRepository.save(existing);
    }

    private String saveImage(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String ext = file.getOriginalFilename() != null
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = "product_" + System.currentTimeMillis() + ext;
        Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR + filename),
                StandardCopyOption.REPLACE_EXISTING);
        return filename;

    }}