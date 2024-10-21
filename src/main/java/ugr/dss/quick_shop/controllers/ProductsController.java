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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.models.ProductDto;
import ugr.dss.quick_shop.services.ProductsRepository;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping({ "", "/", "/index" })
    public String showProductsPage(Model model) {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String createProductPage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);

        return "products/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "products/create";
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

        return "redirect:/products/index";
    }

    @GetMapping("/edit")
    public String editProductPage(Model model, @RequestParam("id") int id) {
        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            System.out.println("Error getting product: " + e.getMessage());
        }
        return "products/edit";
    }

    @PostMapping("/edit")
    public String editProduct(Model model, @Valid @ModelAttribute ProductDto productDto, BindingResult bindingResult,
            @RequestParam("id") int id) {
        try {
            Date updatedAt = new Date();
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (bindingResult.hasErrors()) {
                return "products/edit";
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

        return "redirect:/products/index";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam("id") int id) {
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

        return "redirect:/products/index";
    }

}
