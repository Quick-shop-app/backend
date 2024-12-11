package ugr.dss.quick_shop.controllers;

import java.security.Principal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.Cart;
import ugr.dss.quick_shop.services.CartService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    /**
     * Get cart items for the authenticated user.
     */
    @GetMapping
    public HashMap<String, Object> getCart(@RequestParam String username) {
        HashMap<String, Object> response = new HashMap<>();
        if (username == null || username.isEmpty()) {
            response.put("error", "Invalid user");
            return response;
        }

        Cart cart = cartService.getCartForUser(username);
        if (cart == null) {
            response.put("error", "Cart not found");
            return response;
        }
        response.put("cartItems", cart.getItems().toArray());
        response.put("totalItems", cart.getItems().size());
        return response;
    }

    /**
     * Add an item to the cart.
     */
    @PostMapping("/add")
    public HashMap<String, Object> addToCart(@RequestParam String username, @RequestParam Long productId,
            @RequestParam int quantity) {
        HashMap<String, Object> response = new HashMap<>();
        if (productId == null || quantity <= 0) {
            response.put("error", "Invalid product or quantity");
            return response;
        }

        if (username == null) {
            response.put("error", "User is not authenticated");
            return response;
        }

        cartService.addToCart(username, productId, quantity);
        response.put("success", true);
        response.put("message", "Item added to cart");
        return response;
    }

    /**
     * Remove an item from the cart.
     */
    @PostMapping("/remove")
    public HashMap<String, Object> removeFromCart(@RequestParam String username,
            @RequestParam Long productId) {
        HashMap<String, Object> response = new HashMap<>();
        if (username == null) {
            response.put("error", "User is not authenticated");
            return response;
        }

        cartService.removeFromCart(username, productId);
        response.put("success", true);
        response.put("message", "Item removed from cart");
        return response;
    }

    /**
     * Clear the cart.
     */
    @PostMapping("/clear")
    public HashMap<String, Object> clearCart(@RequestParam String username) {
        HashMap<String, Object> response = new HashMap<>();
        if (username == null) {
            response.put("error", "User is not authenticated");
            return response;
        }

        cartService.clearCart(username);
        response.put("success", true);
        response.put("message", "Cart cleared");
        return response;
    }
}
