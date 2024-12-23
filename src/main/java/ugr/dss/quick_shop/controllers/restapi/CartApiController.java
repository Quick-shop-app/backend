package ugr.dss.quick_shop.controllers.restapi;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.services.CartService;
import ugr.dss.quick_shop.services.OrderService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    /**
     * Get cart items for the authenticated user.
     * 
     * @return
     */
    @GetMapping
    public ResponseEntity<HashMap<String, Object>> getCartItems() {
        HashMap<String, Object> response = new HashMap<>();
        String username = getAuthenticatedUsername();

        if (username == null || username.isEmpty()) {
            response.put("error", "Invalid user");
            return ResponseEntity.badRequest().body(response);
        }

        Cart cart = cartService.getCartForUser(username);
        if (cart == null) {
            response.put("error", "Cart not found");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("data", cart.getItems().toArray());
        response.put("count", cart.getItems().size());
        // total price
        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getTotalPrice())
                .sum();
        response.put("totalPrice", totalPrice);
        return ResponseEntity.ok(response);
    }

    /**
     * Add an item to the cart.
     * 
     * @param productId
     * @param quantity
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<HashMap<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        String username = getAuthenticatedUsername();
        HashMap<String, Object> response = new HashMap<>();
        if (productId == null || quantity <= 0) {
            response.put("error", "Invalid product or quantity");
            return ResponseEntity.badRequest().body(response);
        }

        if (username == null) {
            response.put("error", "User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }

        cartService.addToCart(username, productId, quantity);
        response.put("success", true);
        response.put("message", "Item added to cart");
        return ResponseEntity.ok(response);
    }

    /**
     * Remove an item from the cart.
     * 
     * @param productId
     * @return
     */
    @PostMapping("/remove")
    public ResponseEntity<HashMap<String, Object>> removeFromCart(
            @RequestParam Long productId) {
        HashMap<String, Object> response = new HashMap<>();
        String username = getAuthenticatedUsername();

        if (username == null) {
            response.put("error", "User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }

        cartService.removeFromCart(username, productId);
        response.put("success", true);
        response.put("message", "Item removed from cart");

        return ResponseEntity.ok(response);
    }

    /**
     * Clear the cart.
     * 
     * @return
     */
    @PostMapping("/clear")
    public ResponseEntity<HashMap<String, Object>> clearCart() {
        String username = getAuthenticatedUsername();
        HashMap<String, Object> response = new HashMap<>();

        if (username == null) {
            response.put("error", "User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }

        cartService.clearCart(username);
        response.put("success", true);
        response.put("message", "Cart cleared");
        return ResponseEntity.ok(response);
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

    /**
     * Finalize the cart and create an order.
     * 
     * @return
     */
    @PostMapping("/finalize")
    public ResponseEntity<HashMap<String, Object>> finalizeCart() {
        String username = getAuthenticatedUsername();
        HashMap<String, Object> response = new HashMap<>();

        if (username == null) {
            response.put("error", "User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }

        Cart cart = cartService.getCartForUser(username);
        if (cart.getItems().isEmpty()) {
            response.put("error", "Cart is empty");
            return ResponseEntity.badRequest().body(response);
        }

        Order order = orderService.createOrderFromCart(cart);
        cartService.clearCart(username);
        response.put("data", order);
        response.put("success", true);
        response.put("message", "Order created successfully");
        return ResponseEntity.ok(response);
    }

}