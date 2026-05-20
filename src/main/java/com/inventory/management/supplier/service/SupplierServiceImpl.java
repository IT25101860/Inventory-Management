package com.inventory.management.supplier.service;

import com.inventory.management.supplier.model.Supplier;
import com.inventory.management.supplier.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    @Autowired private SupplierRepository supplierRepository;

    @Override public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByActiveTrue();
    }
    @Override public Optional<Supplier> getSupplierById(Long id) { return supplierRepository.findById(id); }

    @Override
    public Supplier saveSupplier(Supplier supplier) {

        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        existing.setCompanyName(updated.getCompanyName());
        existing.setContactPerson(updated.getContactPerson());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setActive(updated.getActive());
        return supplierRepository.save(existing);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier s = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        s.setActive(false);
        supplierRepository.save(s);
    }

    @Override public List<Supplier> searchSuppliers(String k) { return supplierRepository.findByCompanyNameContainingIgnoreCaseAndActiveTrue(k); }
    @Override public long countSuppliers() { return supplierRepository.count(); }
}