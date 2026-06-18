package com.foodordering.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull(message = "Menu item ID (menuItemId) cannot be null!")
        Long menuItemId,

        @NotNull(message = "Quantity cannot be null!")
        @Min(value = 1, message = "Quantity must be at least 1!")
        Integer quantity
) {}