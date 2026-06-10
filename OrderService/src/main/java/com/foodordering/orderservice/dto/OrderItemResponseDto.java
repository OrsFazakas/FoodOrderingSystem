package com.foodordering.orderservice.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long menuItemId,
        Integer quantity,
        BigDecimal price
) {}