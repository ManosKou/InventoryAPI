package com.example.inventory_api;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> getProducts() {
        return repository.findAll();
    }

    public Optional<ProductResponse> findBySku(String sku) {
        return repository.findBySku(sku);
    }

    public Optional<ProductResponse> createProduct(String sku, String name, int stock) {
        ProductResponse product = new ProductResponse(sku, name, stock);

        try {
            repository.insert(product);
            return Optional.of(product);
        } catch (DuplicateKeyException exception) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<ProductResponse> updateStock(String sku, int stock) {
        if (!repository.updateStock(sku, stock)) {
            return Optional.empty();
        }

        return repository.findBySku(sku);
    }

    public boolean deleteProduct(String sku) {
        return repository.deleteBySku(sku);
    }
}