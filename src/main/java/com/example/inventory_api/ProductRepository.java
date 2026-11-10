package com.example.inventory_api;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductResponse> findAll() {
        return jdbcTemplate.query(
                "SELECT sku, name, stock FROM products ORDER BY sku",
                (row, rowNumber) -> new ProductResponse(
                        row.getString("sku"),
                        row.getString("name"),
                        row.getInt("stock")
                )
        );
    }

    public Optional<ProductResponse> findBySku(String sku) {
        return jdbcTemplate.query(
                "SELECT sku, name, stock FROM products WHERE sku = ?",
                (row, rowNumber) -> new ProductResponse(
                        row.getString("sku"),
                        row.getString("name"),
                        row.getInt("stock")
                ),
                sku
        ).stream().findFirst();
    }

    public void insert(ProductResponse product) {
        jdbcTemplate.update(
                "INSERT INTO products (sku, name, stock) VALUES (?, ?, ?)",
                product.sku(), product.name(), product.stock()
        );
    }

    public boolean updateStock(String sku, int stock) {
        return jdbcTemplate.update(
                "UPDATE products SET stock = ? WHERE sku = ?",
                stock, sku
        ) == 1;
    }

    public boolean deleteBySku(String sku) {
        return jdbcTemplate.update(
                "DELETE FROM products WHERE sku = ?",
                sku
        ) == 1;
    }
}