package com.inventory.management.order.service;

import com.inventory.management.order.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order saveOrder(Order order);
    Order updateOrderStatus(Long id, Order.OrderStatus status);
    void deleteOrder(Long id);
    long countOrders();
    long countPendingOrders();
}