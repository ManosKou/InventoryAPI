package com.example.inventory_api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceTest {

    private EmbeddedDatabase database;
    private ProductService service;

    @BeforeEach
    void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();

        ProductRepository repository = new ProductRepository(new JdbcTemplate(database));
        repository.insert(new ProductResponse("KB-001", "Keyboard", 12));
        repository.insert(new ProductResponse("MS-001", "Mouse", 8));
        service = new ProductService(repository);
    }

    @AfterEach
    void tearDown() {
        database.shutdown();
    }

    @Test
    void deletesOnlyTheRequestedProduct() {
        assertTrue(service.deleteProduct("KB-001"));
        assertTrue(service.findBySku("KB-001").isEmpty());
        assertTrue(service.findBySku("MS-001").isPresent());
    }

    @Test
    void returnsFalseWhenTheSkuDoesNotExist() {
        assertFalse(service.deleteProduct("UNKNOWN"));
    }
}