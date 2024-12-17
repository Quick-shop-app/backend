package ugr.dss.quick_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ugr.dss.quick_shop.models.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
