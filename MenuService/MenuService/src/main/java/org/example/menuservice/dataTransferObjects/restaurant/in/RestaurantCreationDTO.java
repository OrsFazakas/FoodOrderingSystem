package org.example.menuservice.dataTransferObjects.restaurant.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record RestaurantCreationDTO(
        @NotBlank(message = "Name field must not be blank!")
        @NotNull(message = "Name field must not be null!")
        String name,

        String description,

        @NotBlank(message = "City field must not be blank!")
        @NotNull(message = "City field must not be null!")
        String city
) {

}
