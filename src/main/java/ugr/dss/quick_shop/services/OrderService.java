package ugr.dss.quick_shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.cart.CartItem;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.models.order.OrderItem;
import ugr.dss.quick_shop.repositories.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUsername(cart.getUsername());
        order.setTotalPrice(cart.getTotalPrice());

        // Transfer items from Cart to Order
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setPrice(cartItem.getTotalPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        return orderRepository.save(order);
    }
}
