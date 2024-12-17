package ugr.dss.quick_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ugr.dss.quick_shop.models.cart.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Find a cart by username
     * 
     * @param username
     * @return
     */
    Optional<Cart> findByUsername(String username);
}
