package ugr.dss.quick_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ugr.dss.quick_shop.models.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUsername(String username);
}
