package org.example.menuservice.dataTransferObjects.menuItem.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

@Validated
public record MenuItemCreationDTO(
        @NotBlank(message = "Name field must not be blank!")
        @NotNull(message = "Name field must not be null!")
        String name,

        @NotBlank(message = "Price field must not be blank!")
        @NotNull(message = "Price field must not be null!")
        @PositiveOrZero(message = "Price must not be negative!")
        Float price
) {

}
