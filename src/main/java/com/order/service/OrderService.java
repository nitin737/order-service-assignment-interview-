package com.order.service;

import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    List<OrderResponse> listOrders(Optional<OrderStatus> status);
    OrderResponse cancelOrder(Long id);
}
