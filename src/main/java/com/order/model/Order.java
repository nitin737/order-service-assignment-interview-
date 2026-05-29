package com.order.model;

import java.time.Instant;
import java.util.List;

import com.order.dto.OrderItemRequest;
import com.order.dto.OrderRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalAmount;
    private Instant createdAt;
    private Instant updatedAt;

    public static Order toOrder(OrderRequest orderRequest) {
        List<OrderItemRequest> orderItemRequests = orderRequest.getItems();
        List<OrderItem> orderItems = OrderItem.toOrderItems(orderItemRequests);
        double total = orderItems.stream().mapToDouble(OrderItem::getSubTotal).sum();
        return Order.builder()
                .customerId(orderRequest.getCustomerId())
                .items(orderItems)
                .status(OrderStatus.PENDING)
                .totalAmount(total)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
