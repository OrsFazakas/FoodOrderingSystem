package com.foodordering.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull(message = "A menü elem azonosító (menuItemId) nem lehet null!")
        Long menuItemId,

        @NotNull(message = "A mennyiség nem lehet null!")
        @Min(value = 1, message = "A mennyiségnek legalább 1-nek kell lennie!")
        Integer quantity
) {}