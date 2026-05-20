package com.inventory.management.stock.service;

import com.inventory.management.product.model.Product;
import com.inventory.management.product.repository.ProductRepository;
import com.inventory.management.stock.model.StockTransaction;
import com.inventory.management.stock.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockServiceImpl implements StockService {

    @Autowired private StockRepository stockRepository;
    @Autowired private ProductRepository productRepository;

    @Override public List<StockTransaction> getAllTransactions() { return stockRepository.findAllByOrderByCreatedAtDesc(); }
    @Override public Optional<StockTransaction> getTransactionById(Long id) { return stockRepository.findById(id); }

    @Override
    public StockTransaction recordTransaction(StockTransaction transaction) {
        Product product = productRepository.findById(transaction.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        int qty = transaction.getQuantity();
        switch (transaction.getType()) {
            case STOCK_IN -> product.setStockQuantity(product.getStockQuantity() + qty);
            case STOCK_OUT -> {
                if (product.getStockQuantity() < qty)
                    throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
                product.setStockQuantity(product.getStockQuantity() - qty);
            }
            case ADJUSTMENT -> product.setStockQuantity(qty);
        }
        productRepository.save(product);
        return stockRepository.save(transaction);
    }

    @Override public List<StockTransaction> getTransactionsByProduct(Long productId) { return stockRepository.findByProductIdOrderByCreatedAtDesc(productId); }
    @Override public long countTransactions() { return stockRepository.count(); }
}