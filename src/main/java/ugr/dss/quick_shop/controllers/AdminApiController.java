package ugr.dss.quick_shop.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.EditProductDto;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.models.ProductDto;
import ugr.dss.quick_shop.services.DatabaseExportService;
import ugr.dss.quick_shop.services.ProductsRepository;

@Controller
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private DatabaseExportService databaseExportService;

    // @GetMapping({"", "/", "/dashboard"})
    // public String adminDashboard(Model model) {
    // model.addAttribute("products", productsRepository.findAll());
    // return "admin";
    // }
    @GetMapping({ "", "/", "/index" })
    public String showProductsPage(Model model) {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "admin/index";
    }

    @GetMapping("/products/create")
    public String createProductPage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);

        return "admin/product/create";
    }

    @PostMapping("/products/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product/create";
        }

        MultipartFile imageFile = productDto.getImageFile();
        Date createdDate = new Date();
        String fileName = createdDate.getTime() + "_product_" + imageFile.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.out.println("Error saving image file: " + e.getMessage());
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

        return "redirect:/admin";
    }

    @GetMapping("/products/edit")
    public String editProductPage(Model model, @RequestParam("id") Long id) {
        try {
            Product product = productsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            model.addAttribute("product", product);

            EditProductDto productDto = new EditProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            System.out.println("Error getting product: " + e.getMessage());
            return "redirect:/admin";
        }
        return "admin/product/edit";
    }

    @PostMapping("/products/edit")
    public String editProduct(Model model, @Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult,
            @RequestParam("id") Long id) {
        try {
            Date updatedAt = new Date();
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (bindingResult.hasErrors()) {
                return "admin/products/edit";
            }

            if (!productDto.getImageFile().isEmpty() && productDto.getImageFile() != null) {
                MultipartFile imageFile = productDto.getImageFile();
                String fileName = updatedAt.getTime() + "_product_" + imageFile.getOriginalFilename();
                try {
                    String uploadDir = "public/images/";
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    String oldImage = product.getImage();
                    if (oldImage != null && !oldImage.isEmpty()) {
                        Path oldFilePath = uploadPath.resolve(oldImage);
                        Files.delete(oldFilePath);
                    }
                } catch (IOException e) {
                    System.out.println("Error saving image file: " + e.getMessage());
                }
                product.setImage(fileName);
            }

            product.setId(id);
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setUpdatedAt(updatedAt);

            productsRepository.save(product);
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        try {
            Product product = productsRepository.findById(id).get();
            String image = product.getImage();
            if (image != null && !image.isEmpty()) {
                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir);
                Path filePath = uploadPath.resolve(image);
                Files.delete(filePath);
            }
            productsRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    @GetMapping("/products/download-db")
    public void downloadDatabase(HttpServletResponse response) {
        try {
            // Generate the SQL script
            byte[] sqlScript = databaseExportService.exportDatabaseToSql();

            // Set response headers
            response.setContentType("application/sql");
            response.setHeader("Content-Disposition", "attachment; filename=products.sql");

            // Write the SQL script to the response
            response.getOutputStream().write(sqlScript);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}