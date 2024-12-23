package ugr.dss.quick_shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ugr.dss.quick_shop.models.product.Product;

public interface ProductsRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.id DESC")
    List<Product> findAllActiveProducts();

}
