package com.foodordering.orderservice.service;

import com.foodordering.orderservice.client.MenuItemResponse;
import com.foodordering.orderservice.client.MenuServiceClient;
import com.foodordering.orderservice.client.UserServiceClient;
import com.foodordering.orderservice.dto.*;
import com.foodordering.orderservice.exception.ResourceNotFoundException;
import com.foodordering.orderservice.model.*;
import com.foodordering.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;
    private final UserServiceClient userServiceClient;

    public OrderService(OrderRepository orderRepository,
                        MenuServiceClient menuServiceClient,
                        UserServiceClient userServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        // 1. Validate user via User Service
        try {
            Boolean userExists = userServiceClient.checkUserExists(requestDto.userId());
            if (userExists == null || !userExists) {
                throw new ResourceNotFoundException("Order placement failed: User not found with ID: " + requestDto.userId());
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("User Service is unavailable or an error occurred during user validation.");
        }

        Order order = new Order();
        order.setUserId(requestDto.userId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : requestDto.items()) {
            OrderItem item = new OrderItem();
            item.setMenuItemId(itemDto.menuItemId());
            item.setQuantity(itemDto.quantity());

            // 2. Fetch data from Menu Service
            MenuItemResponse menuItem;
            try {
                menuItem = menuServiceClient.getMenuItemById(itemDto.menuItemId());
            } catch (Exception e) {
                throw new ResourceNotFoundException("Menu item not found or Menu Service is unavailable! ID: " + itemDto.menuItemId());
            }

            if (menuItem == null || menuItem.price() == null) {
                throw new ResourceNotFoundException("Menu item details or price are missing! ID: " + itemDto.menuItemId());
            }

            BigDecimal realPrice = menuItem.price();
            item.setPrice(realPrice);

            // Calculate total price
            BigDecimal itemTotal = realPrice.multiply(BigDecimal.valueOf(itemDto.quantity()));
            total = total.add(itemTotal);

            order.addOrderItem(item);
        }

        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);

        return mapToResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        return mapToResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Business rules for status transition
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change the status of a completed or cancelled order!");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDto(updatedOrder);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getMenuItemId(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        return new OrderResponseDto(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                items
        );
    }
}