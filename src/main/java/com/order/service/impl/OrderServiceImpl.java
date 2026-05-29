package com.order.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.exception.OrderNotFoundException;
import com.order.model.Order;
import com.order.model.OrderStatus;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        Order order = Order.toOrder(request);

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.toOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id).map(OrderResponse::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.toOrderResponse(updatedOrder);
    }

    @Override
    public List<OrderResponse> listOrders(Optional<OrderStatus> status) {
        if (status.isPresent()) {
            return orderRepository.findByStatus(status.get()).stream()
                    .map(OrderResponse::toOrderResponse)
                    .collect(Collectors.toList());
        }
        return orderRepository.findAll().stream()
                .map(OrderResponse::toOrderResponse)
                .collect(Collectors.toList());

    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }

}
