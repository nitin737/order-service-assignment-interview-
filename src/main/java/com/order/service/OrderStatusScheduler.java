package com.order.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.order.model.Order;
import com.order.model.OrderStatus;
import com.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusScheduler {

    private OrderRepository orderRepository;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void processOrderStatus() {
        System.out.println("Checking order status");
        int pageSize = 100;

        while (true) {
            List<Order> orders = orderRepository.findByStatusPending(0, pageSize);
            if (orders.isEmpty()) {
                break;
            }
            for (Order order : orders) {
                System.out.println("Processing order " + order.getId() + "with status" + order.getStatus());
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
                System.out.println("Processed order " + order.getId() + "with status" + order.getStatus());
            }
        }

    }
}
