package com.inventory.management.order.service;

import com.inventory.management.order.model.Order;
import com.inventory.management.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orderRepository;

    @Override public List<Order> getAllOrders() { return orderRepository.findAllByOrderByCreatedAtDesc(); }
    @Override public Optional<Order> getOrderById(Long id) { return orderRepository.findById(id); }

    @Override
    public Order saveOrder(Order order) {
        if (orderRepository.existsByOrderNumber(order.getOrderNumber()))
            throw new IllegalArgumentException("Order number already exists.");
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override public long countOrders() { return orderRepository.count(); }
    @Override public long countPendingOrders() { return orderRepository.findByStatus(Order.OrderStatus.PENDING).size(); }
}