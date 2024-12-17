package ugr.dss.quick_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.services.CartService;
import ugr.dss.quick_shop.services.OrderService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    /**
     * Show the cart page with order history.
     */
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        String username = principal.getName();

        // Fetch cart details
        Cart cart = cartService.getCartForUser(username);
        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getTotalPrice())
                .sum();

        // Fetch order history
        List<Order> orderHistory = orderService.getOrderHistory(username);

        System.out.println("Order history: " + orderHistory);

        model.addAttribute("cartTotal", totalPrice);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("orderHistory", orderHistory);

        return "cart";
    }

    /**
     * Add a product to the cart.
     * 
     * @param productId
     * @param quantity
     * @param principal
     * @return
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity, Principal principal) {
        cartService.addToCart(principal.getName(), productId, quantity);
        return "redirect:/cart";
    }

    /**
     * Remove a product from the cart.
     * 
     * @param productId
     * @param principal
     * @return
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, Principal principal) {
        cartService.removeFromCart(principal.getName(), productId);
        return "redirect:/cart";
    }

    /**
     * Clear the cart.
     * 
     * @param principal
     * @return
     */
    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return "redirect:/cart";
    }

    /**
     * Finalize the cart and save it as an order.
     */
    @PostMapping("/finalize")
    public String finalizeCart(Principal principal, Model model) {
        String username = principal.getName();
        Cart cart = cartService.getCartForUser(username);

        if (cart.getItems().isEmpty()) {
            model.addAttribute("error", "Cart is empty");
            return "cart";
        }

        // Save the cart as an order
        Order order = orderService.createOrderFromCart(cart);

        // Clear the cart
        cartService.clearCart(username);

        // Add order details to the model
        model.addAttribute("order", order);
        return "order-summary"; // Show the order summary page
    }
}
