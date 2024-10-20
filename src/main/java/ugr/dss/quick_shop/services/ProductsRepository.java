package ugr.dss.quick_shop.services;

import org.springframework.data.jpa.repository.JpaRepository;

import ugr.dss.quick_shop.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

}
