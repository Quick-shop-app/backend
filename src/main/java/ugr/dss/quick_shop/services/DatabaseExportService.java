package ugr.dss.quick_shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ugr.dss.quick_shop.models.product.Product;
import ugr.dss.quick_shop.repositories.ProductsRepository;

import java.util.List;

@Service
public class DatabaseExportService {

    @Autowired
    private ProductsRepository productsRepository;

    public byte[] exportDatabaseToSql() {
        // Fetch all products from the database
        List<Product> products = productsRepository.findAll();

        // Build SQL script
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("-- SQL Script to insert products\n");
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS products (\n");
        sqlBuilder.append("  id BIGINT AUTO_INCREMENT PRIMARY KEY,\n");
        sqlBuilder.append("  name VARCHAR(255),\n");
        sqlBuilder.append("  brand VARCHAR(255),\n");
        sqlBuilder.append("  category VARCHAR(255),\n");
        sqlBuilder.append("  price DECIMAL(10,2)\n");
        sqlBuilder.append(");\n\n");

        for (Product product : products) {
            sqlBuilder.append(String.format(
                    "INSERT INTO products (id, name, brand, category, price) VALUES (%d, '%s', '%s', '%s', %.2f);\n",
                    product.getId(),
                    product.getName().replace("'", "''"), // Escape single quotes
                    product.getBrand().replace("'", "''"),
                    product.getCategory().replace("'", "''"),
                    product.getPrice()));
        }

        return sqlBuilder.toString().getBytes();
    }
}
