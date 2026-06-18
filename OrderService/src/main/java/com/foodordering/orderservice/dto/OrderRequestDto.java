package com.foodordering.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequestDto(
        @NotNull(message = "User ID (userId) cannot be null!")
        Long userId,

        @NotEmpty(message = "The order must contain at least one item!")
        @Valid
        List<OrderItemRequestDto> items
) {}