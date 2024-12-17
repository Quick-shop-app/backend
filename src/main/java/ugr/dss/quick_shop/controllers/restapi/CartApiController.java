package ugr.dss.quick_shop.controllers.restapi;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.services.CartService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    /**
     * Get cart items for the authenticated user.
     * 
     * @return
     */
    @GetMapping
    public HashMap<String, Object> getCart() {
        HashMap<String, Object> response = new HashMap<>();
        String username = getAuthenticatedUsername();

        if (username == null || username.isEmpty()) {
            response.put("error", "Invalid user");
            return response;
        }

        Cart cart = cartService.getCartForUser(username);
        if (cart == null) {
            response.put("error", "Cart not found");
            return response;
        }
        response.put("data", cart.getItems().toArray());
        response.put("count", cart.getItems().size());
        return response;
    }

    /**
     * Add an item to the cart.
     * 
     * @param productId
     * @param quantity
     * @return
     */
    @PostMapping("/add")
    public HashMap<String, Object> addToCart(@RequestParam Long productId,
            @RequestParam int quantity) {
        String username = getAuthenticatedUsername();
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
     * 
     * @param productId
     * @return
     */
    @PostMapping("/remove")
    public HashMap<String, Object> removeFromCart(
            @RequestParam Long productId) {
        HashMap<String, Object> response = new HashMap<>();
        String username = getAuthenticatedUsername();

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
     * 
     * @return
     */
    @PostMapping("/clear")
    public HashMap<String, Object> clearCart() {
        String username = getAuthenticatedUsername();
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

    /**
     * Utility method to get the currently authenticated username.
     */
    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return auth.getName();
    }
}
