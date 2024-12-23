package ugr.dss.quick_shop.controllers.restapi;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.product.Product;
import ugr.dss.quick_shop.repositories.ProductsRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ProductsRepository productsRepository;

    /**
     * Get all products
     * 
     * @return
     */
    @GetMapping("/products")
    public HashMap<String, Object> products() {
        HashMap<String, Object> response = new HashMap<>();
        List<Product> products = productsRepository.findAllActiveProducts();
        response.put("count", products.size());
        response.put("data", products);
        return response;
    }

}
