package com.order.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {

    private String customerId;
    private List<OrderItemRequest> items;

}
