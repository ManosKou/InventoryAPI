# Inventory API

A REST API for managing product inventory by SKU. It supports creating,
listing and deleting products, looking up a product by SKU, and setting
its stock quantity.

Built with Java 21, Spring Boot, Spring JDBC, H2, Maven and JUnit.

## Run locally

Install JDK 21, then open PowerShell in the directory containing `pom.xml`.

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`. On first startup, the
application creates the `products` table from `schema.sql`. The inventory
starts empty. Changes are stored in `data/inventory.mv.db` and survive
application restarts.

## Endpoints

| Method | Path | Success response |
| --- | --- | --- |
| `GET` | `/api/products` | `200 OK` with the product list |
| `GET` | `/api/products/{sku}` | `200 OK` with the matching product |
| `POST` | `/api/products` | `201 Created` with the product and a `Location` header |
| `PATCH` | `/api/products/{sku}/stock` | `200 OK` with the updated product |
| `DELETE` | `/api/products/{sku}` | `204 No Content` |

Invalid input returns `400 Bad Request`. A missing product returns
`404 Not Found`. Creating a product with an existing SKU returns
`409 Conflict`. Stock must be a non-negative integer.

## Example workflow

With the application running, open a second PowerShell terminal and create
a product:

```powershell
'{"sku":"HD-001","name":"Headphones","stock":5}' |
    curl.exe -i -X POST -H "Content-Type: application/json" --data-binary "@-" http://localhost:8080/api/products
```

Set its stock to `8`, then retrieve it:

```powershell
'{"stock":8}' |
    curl.exe -i -X PATCH -H "Content-Type: application/json" --data-binary "@-" http://localhost:8080/api/products/HD-001/stock

curl.exe -i http://localhost:8080/api/products/HD-001
```

To verify persistence, restart the application and repeat the `GET`.
The response should still contain `"stock":8`.

## Run the tests

```powershell
.\mvnw.cmd clean test
```

The test suite checks application startup and verifies that deleting a
product preserves other products and handles unknown SKUs. The
`ProductServiceTest` uses a separate in-memory H2 database for each test.