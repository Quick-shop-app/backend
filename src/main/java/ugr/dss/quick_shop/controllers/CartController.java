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

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        Cart cart = cartService.getCartForUser(principal.getName());
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Integer productId, @RequestParam int quantity, Principal principal) {
        cartService.addToCart(principal.getName(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Integer productId, Principal principal) {
        cartService.removeFromCart(principal.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return "redirect:/cart";
    }
}
