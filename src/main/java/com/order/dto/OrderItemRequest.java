package com.order.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String productId;
    private int quantity;
    private double price;
}
