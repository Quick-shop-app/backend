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
import ugr.dss.quick_shop.models.product.EditProductDto;
import ugr.dss.quick_shop.models.product.Product;
import ugr.dss.quick_shop.models.product.ProductDto;
import ugr.dss.quick_shop.services.DatabaseExportService;
import ugr.dss.quick_shop.services.ProductsRepository;
import ugr.dss.quick_shop.utils.FileUploadUtil;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private DatabaseExportService databaseExportService;

    /**
     * Show the admin dashboard.
     * 
     * @param model
     * @return
     */
    @GetMapping({ "", "/", "/index" })
    public String showProductsPage(Model model) {
        List<Product> products = productsRepository.findAllActiveProducts();
        model.addAttribute("products", products);
        return "admin/index";
    }

    /**
     * Show the create product page.
     * 
     * @param model
     * @return
     */
    @GetMapping("/products/create")
    public String createProductPage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);

        return "admin/product/create";
    }

    /**
     * Create a new product.
     * 
     * @param productDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/products/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product/create";
        }

        MultipartFile imageFile = productDto.getImageFile();
        Date createdDate = new Date();
        String fileName = null;
        try {
            fileName = FileUploadUtil.saveImage(imageFile, createdDate);
        } catch (IOException e) {
            System.out.println("Error saving image file: " + e.getMessage());
            return "admin/product/create";
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

    /**
     * Show the edit product page.
     * 
     * @param model
     * @param id
     * @return
     */
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

    /**
     * Edit a product.
     * 
     * @param model
     * @param productDto
     * @param bindingResult
     * @param id            // Product ID
     * @return
     */
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
                try {
                    MultipartFile imageFile = productDto.getImageFile();
                    String fileName = FileUploadUtil.replaceImage(imageFile, imageFile.getOriginalFilename(),
                            updatedAt);
                    product.setImage(fileName);
                } catch (IOException e) {
                    System.out.println("Error saving image file: " + e.getMessage());
                }
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

    /**
     * Delete a product.
     * 
     * @param id
     * @return
     */
    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        try {
            Product product = productsRepository.findById(id).get();

            product.setActive(false);
            productsRepository.save(product);
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    /**
     * Download the database as an SQL script.
     * 
     * @param response
     */
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