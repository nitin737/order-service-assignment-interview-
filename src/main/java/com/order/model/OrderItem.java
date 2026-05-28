package com.order.model;

import java.util.List;

import com.order.dto.OrderItemRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private int quantity;
    private double price;
    private double subTotal;

    public static List<OrderItem> toOrderItems(List<OrderItemRequest> orderItemRequests) {

        return orderItemRequests.stream().map(orderItemRequest -> OrderItem.builder()
                .productId(orderItemRequest.getProductId())
                .quantity(orderItemRequest.getQuantity())
                .price(orderItemRequest.getPrice())
                .subTotal(orderItemRequest.getPrice() * orderItemRequest.getQuantity())
                .build())
                .toList();

    }

}
