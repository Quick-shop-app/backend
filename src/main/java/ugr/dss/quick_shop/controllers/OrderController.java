package ugr.dss.quick_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.services.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/finalize")
    public Order finalizeOrder(@RequestBody Cart cart) {
        return orderService.createOrderFromCart(cart);
    }
}
