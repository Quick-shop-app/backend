package ugr.dss.quick_shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ugr.dss.quick_shop.models.Cart;
import ugr.dss.quick_shop.models.CartItem;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.repositories.CartRepository;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductsRepository productRepository;

    /**
     * Get the cart for the current user. Create a new cart if none exists.
     */
    public Cart getCartForUser(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUsername(username);
                    cartRepository.save(newCart);
                    return newCart;
                });
    }

    /**
     * Add a product to the cart or update the quantity if it already exists.
     */
    public void addToCart(String username, Long productId, int quantity) {
        Cart cart = getCartForUser(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setTotalPrice(item.getQuantity() * product.getPrice());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setTotalPrice(product.getPrice() * quantity);
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    /**
     * Remove a product from the cart.
     */
    public void removeFromCart(String username, Long productId) {
        Cart cart = getCartForUser(username);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    /**
     * Clear all items from the cart.
     */
    public void clearCart(String username) {
        Cart cart = getCartForUser(username);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum());
    }
}
