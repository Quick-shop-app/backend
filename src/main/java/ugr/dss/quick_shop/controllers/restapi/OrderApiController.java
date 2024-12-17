package ugr.dss.quick_shop.controllers.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/finalize")
    public Order finalizeOrder(@RequestBody Cart cart) {
        return orderService.createOrderFromCart(cart);
    }
}
