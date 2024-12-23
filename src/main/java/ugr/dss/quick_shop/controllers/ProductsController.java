package ugr.dss.quick_shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ugr.dss.quick_shop.models.product.Product;
import ugr.dss.quick_shop.repositories.ProductsRepository;

@Controller
@RequestMapping({ "/products" })
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    /**
     * Show the products page.
     * 
     * @param model
     * @return
     */
    @GetMapping({ "", "/", "/index" })
    public String showProductsPage(Model model) {
        List<Product> products = productsRepository.findAllActiveProducts();
        model.addAttribute("products", products);
        return "products";
    }

}
