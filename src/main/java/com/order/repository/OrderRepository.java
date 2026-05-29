package com.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.order.model.Order;
import com.order.model.OrderStatus;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);

    Optional<Order> findById(Long id);

    @Query(value = "SELECT * FROM orders WHERE status = 'PENDING' LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Order> findByStatusPending(long offset, int pageSize);
}
