package org.example.menuservice.dataTransferObjects.restaurant.out;

public record RestaurantResponseDTO(
        Long id,
        String name,
        String description,
        String city
) {
}
