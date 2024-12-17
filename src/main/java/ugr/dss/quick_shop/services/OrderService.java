package ugr.dss.quick_shop.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ugr.dss.quick_shop.models.cart.Cart;
import ugr.dss.quick_shop.models.cart.CartItem;
import ugr.dss.quick_shop.models.order.Order;
import ugr.dss.quick_shop.models.order.OrderItem;
import ugr.dss.quick_shop.repositories.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUsername(cart.getUsername());
        order.setTotalPrice(cart.getTotalPrice());

        // Convert CartItems to OrderItems
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrderHistory(String username) {
        return orderRepository.findByUsername(username);
    }
}
