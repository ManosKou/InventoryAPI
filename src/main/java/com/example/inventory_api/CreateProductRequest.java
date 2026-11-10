package com.example.inventory_api;

public record CreateProductRequest(String sku, String name, Integer stock) {
}