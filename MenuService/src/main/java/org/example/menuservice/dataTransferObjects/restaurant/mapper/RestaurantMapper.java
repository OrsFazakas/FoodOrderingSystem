package org.example.menuservice.dataTransferObjects.restaurant.mapper;

import org.example.menuservice.dataTransferObjects.restaurant.in.RestaurantCreationDTO;
import org.example.menuservice.dataTransferObjects.restaurant.out.RestaurantResponseDTO;
import org.example.menuservice.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public Restaurant toRestaurant(RestaurantCreationDTO restaurantCreationDTO){
        return Restaurant.builder()
                .name(restaurantCreationDTO.name())
                .description(restaurantCreationDTO.description())
                .city(restaurantCreationDTO.city())
                .build();
    }

    public RestaurantResponseDTO toResponseDTO(Restaurant restaurant){
        return new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCity()
        );
    }
}
