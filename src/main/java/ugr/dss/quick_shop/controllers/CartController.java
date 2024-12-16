package ugr.dss.quick_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ugr.dss.quick_shop.models.Cart;
import ugr.dss.quick_shop.services.CartService;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Show the cart page.
     * 
     * @param model
     * @param principal
     * @return
     */
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        Cart cart = cartService.getCartForUser(principal.getName());
        cart.getItems().forEach(item -> {
            System.out.println("Cart Item: " + item);
            System.out.println("Product: " + item.getProduct());
        });
        model.addAttribute("cartItems", cart.getItems());
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
}
