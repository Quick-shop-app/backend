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

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.models.ProductDto;
import ugr.dss.quick_shop.services.ProductsRepository;

@Controller
@RequestMapping({ "/products" })
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping({ "", "/", "/index" })
    public String showProductsPage(Model model) {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products";
    }

    // @PostMapping("/add-to-cart")
    // public String addToCart(@RequestParam("productId") Long productId, Model
    // model) {
    // Product product = productsRepository.findById(productId).orElse(null);
    // if (product == null) {
    // model.addAttribute("error", "Product not found");
    // return "redirect:/products";
    // }
    // // Logic to add the product to the cart
    // // For example, you can add the product to a session attribute representing
    // the
    // // cart
    // // session.setAttribute("cart", cart);

    // model.addAttribute("message", "Product added to cart successfully");
    // return "redirect:/products";
    // }

}
