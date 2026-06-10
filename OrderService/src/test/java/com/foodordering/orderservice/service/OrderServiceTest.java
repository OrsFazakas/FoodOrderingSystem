package com.foodordering.orderservice.service;

import com.foodordering.orderservice.client.MenuItemResponse;
import com.foodordering.orderservice.client.MenuServiceClient;
import com.foodordering.orderservice.client.UserServiceClient;
import com.foodordering.orderservice.dto.OrderItemRequestDto;
import com.foodordering.orderservice.dto.OrderRequestDto;
import com.foodordering.orderservice.dto.OrderResponseDto;
import com.foodordering.orderservice.exception.ResourceNotFoundException;
import com.foodordering.orderservice.model.Order;
import com.foodordering.orderservice.model.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuServiceClient menuServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDto validRequestDto;
    private MenuItemResponse mockMenuItem;

    @BeforeEach
    void setUp() {
        OrderItemRequestDto itemDto = new OrderItemRequestDto(101L, 2);
        validRequestDto = new OrderRequestDto(1L, List.of(itemDto));

        mockMenuItem = new MenuItemResponse(101L, "Pizza", new BigDecimal("2500.00"));
    }

    @Test
    void createOrder_Success() {
        // GIVEN
        when(userServiceClient.checkUserExists(1L)).thenReturn(true);
        when(menuServiceClient.getMenuItemById(101L)).thenReturn(mockMenuItem);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(123L);
            return order;
        });

        // WHEN
        OrderResponseDto response = orderService.createOrder(validRequestDto);

        // THEN
        assertNotNull(response);
        assertEquals(123L, response.id());
        assertEquals(OrderStatus.CREATED, response.status());
        assertEquals(new BigDecimal("5000.00"), response.totalPrice());

        verify(userServiceClient, times(1)).checkUserExists(1L);
        verify(menuServiceClient, times(1)).getMenuItemById(101L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ThrowsException_WhenUserNotFound() {
        // GIVEN
        when(userServiceClient.checkUserExists(1L)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(validRequestDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        // GIVEN
        Order order = new Order();
        order.setId(123L);
        order.setUserId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(new BigDecimal("5000.00"));

        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        // WHEN
        OrderResponseDto response = orderService.getOrderById(123L);

        // THEN
        assertNotNull(response);
        assertEquals(123L, response.id());
        assertEquals(new BigDecimal("5000.00"), response.totalPrice());
    }

    @Test
    void getOrderById_ThrowsException_WhenOrderNotFound() {
        // GIVEN
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void updateOrderStatus_Success() {
        // GIVEN
        Order order = new Order();
        order.setId(123L);
        order.setStatus(OrderStatus.CREATED);
        order.setUserId(1L);
        order.setTotalPrice(BigDecimal.TEN);

        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        OrderResponseDto response = orderService.updateOrderStatus(123L, OrderStatus.CONFIRMED);

        // THEN
        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.status());
    }

    @Test
    void updateOrderStatus_ThrowsException_WhenOrderAlreadyCompleted() {
        // GIVEN
        Order order = new Order();
        order.setId(123L);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        // WHEN & THEN
        assertThrows(IllegalStateException.class, () -> orderService.updateOrderStatus(123L, OrderStatus.CANCELLED));
    }
}