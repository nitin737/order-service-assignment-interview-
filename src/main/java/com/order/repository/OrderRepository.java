package com.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.order.model.Order;
import com.order.model.OrderStatus;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);

    Optional<Order> findById(Long id);
}
