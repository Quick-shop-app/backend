package ugr.dss.quick_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ugr.dss.quick_shop.models.cart.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
