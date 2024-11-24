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

    public Cart getCartForUser(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> new Cart(username));
    }

    public void addToCart(String username, Integer productId, int quantity) {
        Cart cart = getCartForUser(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId() == productId).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setTotalPrice(item.getProduct().getPrice() * item.getQuantity());
        } else {
            CartItem item = new CartItem(cart, product, quantity, product.getPrice() *
                    quantity);
            cart.getItems().add(item);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    public void removeFromCart(String username, Integer productId) {
        Cart cart = getCartForUser(username);
        cart.getItems().removeIf(item -> item.getProduct().getId() == productId);
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    public void clearCart(String username) {
        Cart cart = getCartForUser(username);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        cart.setTotalPrice(cart.getItems().stream().mapToDouble(CartItem::getTotalPrice).sum());
    }
}
