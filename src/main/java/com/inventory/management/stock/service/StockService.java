package com.inventory.management.stock.service;

import com.inventory.management.stock.model.StockTransaction;
import java.util.List;
import java.util.Optional;

public interface StockService {
    List<StockTransaction> getAllTransactions();
    Optional<StockTransaction> getTransactionById(Long id);
    StockTransaction recordTransaction(StockTransaction transaction);
    List<StockTransaction> getTransactionsByProduct(Long productId);
    long countTransactions();
}