package com.inventory.management.supplier.service;

import com.inventory.management.supplier.model.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Optional<Supplier> getSupplierById(Long id);
    Supplier saveSupplier(Supplier supplier);
    Supplier updateSupplier(Long id, Supplier supplier);
    void deleteSupplier(Long id);
    List<Supplier> searchSuppliers(String keyword);
    long countSuppliers();
}