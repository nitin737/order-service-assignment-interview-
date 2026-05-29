package com.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.order.dto.OrderItemRequest;
import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.exception.OrderNotFoundException;
import com.order.model.Order;
import com.order.model.OrderItem;
import com.order.model.OrderStatus;
import com.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    class CreateOrderTests {

        @Test
        void shouldSaveAndReturnOrderResponse() {
            // Arrange
            OrderRequest request = new OrderRequest();
            request.setCustomerId("cust-123");

            OrderItemRequest itemRequest = new OrderItemRequest();
            itemRequest.setProductId("prod-456");
            itemRequest.setQuantity(2);
            itemRequest.setPrice(50.0);
            request.setItems(List.of(itemRequest));

            OrderItem expectedItem = OrderItem.builder()
                    .id(1L)
                    .productId("prod-456")
                    .quantity(2)
                    .price(50.0)
                    .subTotal(100.0)
                    .build();

            Order expectedSavedOrder = Order.builder()
                    .id(100L)
                    .customerId("cust-123")
                    .items(List.of(expectedItem))
                    .status(OrderStatus.PENDING)
                    .totalAmount(100.0)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(orderRepository.save(any(Order.class))).thenReturn(expectedSavedOrder);

            // Act
            OrderResponse response = orderService.createOrder(request);

            // Assert
            assertEquals(100L, response.getId());
            assertEquals("cust-123", response.getCustomerId());
            assertEquals(OrderStatus.PENDING, response.getStatus());
            assertEquals(100.0, response.getTotalAmount());
            assertEquals(1, response.getItems().size());
            assertEquals("prod-456", response.getItems().get(0).getProductId());

            verify(orderRepository, times(1)).save(any(Order.class));
        }
    }

    @Nested
    class GetOrderByIdTests {

        @Test
        void shouldReturnOrderResponse_WhenOrderExists() throws OrderNotFoundException {
            // Arrange
            Long orderId = 100L;
            Order mockOrder = Order.builder()
                    .id(orderId)
                    .customerId("cust-123")
                    .status(OrderStatus.PENDING)
                    .totalAmount(250.0)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

            // Act
            OrderResponse response = orderService.getOrderById(orderId);

            // Assert
            assertNotNull(response);
            assertEquals(orderId, response.getId());
            assertEquals("cust-123", response.getCustomerId());
            assertEquals(OrderStatus.PENDING, response.getStatus());
            verify(orderRepository, times(1)).findById(orderId);
        }

        @Test
        void shouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
            // Arrange
            Long orderId = 999L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // Act & Assert
            OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
                orderService.getOrderById(orderId);
            });

            assertEquals("Order Not Found", exception.getMessage());
            verify(orderRepository, times(1)).findById(orderId);
        }
    }

    @Nested
    class UpdateOrderStatusTests {

        @Test
        void shouldUpdateStatusAndReturnResponse_WhenOrderExists() throws OrderNotFoundException {
            // Arrange
            Long orderId = 100L;
            Order existingOrder = Order.builder()
                    .id(orderId)
                    .customerId("cust-123")
                    .status(OrderStatus.PENDING)
                    .totalAmount(150.0)
                    .createdAt(Instant.now().minusSeconds(3600))
                    .updatedAt(Instant.now().minusSeconds(3600))
                    .build();

            Order savedOrder = Order.builder()
                    .id(orderId)
                    .customerId("cust-123")
                    .status(OrderStatus.SHIPPED)
                    .totalAmount(150.0)
                    .createdAt(existingOrder.getCreatedAt())
                    .updatedAt(Instant.now())
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // Act
            OrderResponse response = orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

            // Assert
            assertEquals(orderId, response.getId());
            assertEquals(OrderStatus.SHIPPED, response.getStatus());

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderRepository, times(1)).save(existingOrder);

            // Check that the existingOrder's status was changed before saving
            assertEquals(OrderStatus.SHIPPED, existingOrder.getStatus());
        }

        @Test
        void shouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
            // Arrange
            Long orderId = 999L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(OrderNotFoundException.class, () -> {
                orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);
            });

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    // =========================================================================
    // 4. Group: listOrders
    // =========================================================================
    @Nested
    class ListOrdersTests {

        @Test
        void shouldReturnFilteredOrders_WhenStatusIsProvided() {
            // Arrange
            OrderStatus status = OrderStatus.PENDING;
            Order order1 = Order.builder().id(1L).status(status).build();
            Order order2 = Order.builder().id(2L).status(status).build();

            when(orderRepository.findByStatus(status)).thenReturn(List.of(order1, order2));

            // Act
            List<OrderResponse> responseList = orderService.listOrders(Optional.of(status));

            // Assert
            assertNotNull(responseList);
            assertEquals(2, responseList.size());
            assertEquals(1L, responseList.get(0).getId());
            assertEquals(2L, responseList.get(1).getId());

            verify(orderRepository, times(1)).findByStatus(status);
            verify(orderRepository, never()).findAll();
        }

        @Test
        void shouldReturnAllOrders_WhenStatusIsEmpty() {
            // Arrange
            Order order1 = Order.builder().id(1L).status(OrderStatus.PENDING).build();
            Order order2 = Order.builder().id(2L).status(OrderStatus.SHIPPED).build();

            when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

            // Act
            List<OrderResponse> responseList = orderService.listOrders(Optional.empty());

            // Assert
            assertNotNull(responseList);
            assertEquals(2, responseList.size());
            verify(orderRepository, times(1)).findAll();
            verify(orderRepository, never()).findByStatus(any(OrderStatus.class));
        }
    }

    @Nested
    class CancelOrderTests {

        @Test
        void shouldUpdateStatusToCancelledAndReturnResponse_WhenOrderExists() throws OrderNotFoundException {
            // Arrange
            Long orderId = 100L;
            Order existingOrder = Order.builder()
                    .id(orderId)
                    .customerId("cust-123")
                    .status(OrderStatus.PENDING)
                    .totalAmount(150.0)
                    .build();

            Order savedOrder = Order.builder()
                    .id(orderId)
                    .customerId("cust-123")
                    .status(OrderStatus.CANCELLED)
                    .totalAmount(150.0)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // Act
            OrderResponse response = orderService.cancelOrder(orderId);

            // Assert
            assertNotNull(response);
            assertEquals(orderId, response.getId());
            assertEquals(OrderStatus.CANCELLED, response.getStatus());

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderRepository, times(1)).save(existingOrder);
        }
    }
}
