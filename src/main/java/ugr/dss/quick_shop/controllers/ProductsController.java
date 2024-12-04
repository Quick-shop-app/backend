package ugr.dss.quick_shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ugr.dss.quick_shop.models.Product;
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

}
