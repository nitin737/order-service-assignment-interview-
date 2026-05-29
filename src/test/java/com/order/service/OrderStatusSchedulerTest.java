package com.order.service;

import com.order.model.Order;
import com.order.model.OrderStatus;
import com.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderStatusSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderStatusScheduler orderStatusScheduler;

    @Test
    void shouldTransitionPendingToProcessing_WhenPendingOrdersExist() {
        // Arrange
        Order order1 = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        Order order2 = Order.builder().id(2L).status(OrderStatus.PENDING).build();
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findByStatusPending(0, 100))
                .thenReturn(orders)
                .thenReturn(Collections.emptyList());

        // Act
        orderStatusScheduler.processOrderStatus();

        // Assert
        assertEquals(OrderStatus.PROCESSING, order1.getStatus());
        assertEquals(OrderStatus.PROCESSING, order2.getStatus());
        verify(orderRepository, times(1)).saveAll(orders);
    }

    @Test
    void shouldDoNothing_WhenNoPendingOrdersExist() {
        // Arrange
        when(orderRepository.findByStatusPending(0, 100)).thenReturn(Collections.emptyList());

        // Act
        orderStatusScheduler.processOrderStatus();

        // Assert
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldExitCleanly_WhenSaveAllThrowsException() {
        // Arrange
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        List<Order> orders = List.of(order);

        when(orderRepository.findByStatusPending(0, 100)).thenReturn(orders);
        when(orderRepository.saveAll(orders)).thenThrow(new RuntimeException("Database error"));

        // Act
        orderStatusScheduler.processOrderStatus();

        // Assert (should execute without propagation)
        verify(orderRepository, times(1)).saveAll(orders);
    }
}
