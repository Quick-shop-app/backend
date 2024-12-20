package ugr.dss.quick_shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ugr.dss.quick_shop.models.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.username = ?1 ORDER BY o.id DESC")
    List<Order> findByUsername(String username);
}
