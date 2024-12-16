package ugr.dss.quick_shop.controllers.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.EditProductDto;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.models.ProductDto;
import ugr.dss.quick_shop.services.DatabaseExportService;
import ugr.dss.quick_shop.services.ProductsRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminApiController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private DatabaseExportService databaseExportService;

    @PostMapping(value = "/products", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("product") ProductDto productDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("error", "Validation failed");
            bindingResult.getAllErrors().forEach(error -> {
                response.put(error.getCode(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(response);
        }

        String fileName = null;
        Date createdDate = new Date();

        // Handle image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            fileName = createdDate.getTime() + "_product_" + imageFile.getOriginalFilename();
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                response.put("error", "Error saving image file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImage(fileName);
        product.setCreatedAt(createdDate);
        productsRepository.save(product);

        response.put("success", true);
        response.put("product", product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        Optional<Product> productOpt = productsRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }
        return ResponseEntity.ok(productOpt.get());
    }

    @PutMapping(value = "/products/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> editProduct(
            @PathVariable("id") Long id,
            @Valid @RequestPart("product") ProductDto productDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("error", "Validation failed");
            bindingResult.getAllErrors().forEach(error -> {
                response.put(error.getCode(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Product> productOpt = productsRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Product product = productOpt.get();
        Date updatedAt = new Date();

        // If a new image is provided, handle image replacement
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = updatedAt.getTime() + "_product_" + imageFile.getOriginalFilename();
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Delete old image if exists
                String oldImage = product.getImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldFilePath = uploadPath.resolve(oldImage);
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                }
                product.setImage(fileName);
            } catch (IOException e) {
                response.put("error", "Error saving image file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setUpdatedAt(updatedAt);

        productsRepository.save(product);
        response.put("success", true);
        response.put("product", product);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        Optional<Product> productOpt = productsRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Product product = productOpt.get();
        String image = product.getImage();
        if (image != null && !image.isEmpty()) {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(image);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error deleting product image: " + e.getMessage()));
            }
        }
        productsRepository.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    @GetMapping("/products/download-db")
    public void downloadDatabase(HttpServletResponse response) {
        try {
            byte[] sqlScript = databaseExportService.exportDatabaseToSql();
            response.setContentType("application/sql");
            response.setHeader("Content-Disposition", "attachment; filename=products.sql");
            response.getOutputStream().write(sqlScript);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}