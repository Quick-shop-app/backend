package ugr.dss.quick_shop.controllers.restapi;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.product.Product;
import ugr.dss.quick_shop.models.product.ProductDto;
import ugr.dss.quick_shop.services.DatabaseExportService;
import ugr.dss.quick_shop.services.ProductsRepository;
import ugr.dss.quick_shop.utils.FileUploadUtil;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private DatabaseExportService databaseExportService;

    /**
     * Get all products.
     *
     * @return List of products
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productsRepository.findAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get a single product by ID.
     *
     * @param id Product ID
     * @return Product or NOT_FOUND status
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Create a new product.
     *
     * @param productDto Product DTO
     * @return Created product or INTERNAL_SERVER_ERROR status
     */
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@ModelAttribute @Valid ProductDto productDto) {
        try {
            Date createdAt = new Date();

            Product product = new Product();
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(createdAt);

            // Handle image upload
            MultipartFile imageFile = productDto.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = FileUploadUtil.saveImage(imageFile, createdAt);
                    product.setImage(fileName);
                } catch (IOException e) {
                    System.out.println("Error saving image file: " + e.getMessage());
                }
            }

            Product savedProduct = productsRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a product by ID.
     * 
     * @param id
     * @param productDto
     * @return Updated product or NOT_FOUND status
     */
    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @ModelAttribute @Valid ProductDto productDto) {
        try {
            Date updatedAt = new Date();
            Product existingProduct = productsRepository.findById(id).orElse(null);
            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Update product details
            existingProduct.setName(productDto.getName());
            existingProduct.setBrand(productDto.getBrand());
            existingProduct.setCategory(productDto.getCategory());
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setDescription(productDto.getDescription());
            existingProduct.setUpdatedAt(updatedAt);

            // Update image if a new file is provided
            MultipartFile imageFile = productDto.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = FileUploadUtil.replaceImage(imageFile, imageFile.getOriginalFilename(),
                            updatedAt);
                    existingProduct.setImage(fileName);
                } catch (IOException e) {
                    System.out.println("Error saving image file: " + e.getMessage());
                }
            }

            existingProduct.setUpdatedAt(new Date());
            Product updatedProduct = productsRepository.save(existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a product by ID.
     *
     * @param id Product ID
     * @return NO_CONTENT status if successful, or NOT_FOUND if the product doesn't
     *         exist
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            // Check if the product exists
            Product product = productsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Soft delete the product
            product.setActive(false);
            productsRepository.save(product);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the product.");
        }
    }

    /**
     * Download the database as an SQL script.
     *
     * @return SQL file as a byte array
     */
    @GetMapping("/products/download-db")
    public ResponseEntity<byte[]> downloadDatabase() {
        byte[] sqlScript = databaseExportService.exportDatabaseToSql();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=products.sql")
                .body(sqlScript);

    }
}
