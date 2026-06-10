package com.foodordering.orderservice.dto;

import com.foodordering.orderservice.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponseDto> orderItems
) {}