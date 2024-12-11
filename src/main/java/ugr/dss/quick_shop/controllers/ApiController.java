package ugr.dss.quick_shop.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.services.ProductsRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping("/products")
    public HashMap<String, Object> products() {
        HashMap<String, Object> response = new HashMap<>();
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        response.put("count", products.size());
        response.put("data", products);
        return response;
    }

}
