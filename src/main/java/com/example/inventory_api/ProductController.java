package com.example.inventory_api;

import java.util.List;
import java.net.URI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(
            @PathVariable("sku") String sku
    ) {
        return ResponseEntity.of(productService.findBySku(sku));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody CreateProductRequest request
    ) {
        if (request.sku() == null || request.sku().isBlank()
                || request.name() == null || request.name().isBlank()
                || request.stock() == null || request.stock() < 0) {
            return ResponseEntity.badRequest().build();
        }

        String sku = request.sku().strip();
        String name = request.name().strip();

        if (sku.length() > 64 || name.length() > 255 || !sku.matches("[A-Za-z0-9-]+")) {
            return ResponseEntity.badRequest().build();
        }

        var created = productService.createProduct(sku, name, request.stock());

        if (created.isEmpty()) {
            return ResponseEntity.status(409).build();
        }

        URI location = UriComponentsBuilder.fromPath("/api/products/{sku}")
                .buildAndExpand(sku)
                .toUri();

        return ResponseEntity.created(location).body(created.get());
    }

    @PatchMapping("/{sku}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable("sku") String sku,
            @RequestBody UpdateStockRequest request
    ) {
        if (request.stock() == null || request.stock() < 0) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.of(productService.updateStock(sku, request.stock()));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("sku") String sku) {
        if (productService.deleteProduct(sku)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}