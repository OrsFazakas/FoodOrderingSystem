package com.foodordering.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequestDto(
        @NotNull(message = "A felhasználó azonosító (userId) nem lehet null!")
        Long userId,

        @NotEmpty(message = "A rendelésnek legalább egy tételt tartalmaznia kell!")
        @Valid
        List<OrderItemRequestDto> items
) {}