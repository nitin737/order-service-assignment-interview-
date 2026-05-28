package com.order.dto;

import java.util.List;

import com.order.model.Order;
import com.order.model.OrderItem;
import com.order.model.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private double totalAmount;

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .items(order.getItems())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
