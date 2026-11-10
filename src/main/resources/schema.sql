CREATE TABLE IF NOT EXISTS products (
                                        sku VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0)
    );