package org.example.menuservice.exception.restaurant;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException() {
        super("No restaurants found!");
    }
    public RestaurantNotFoundException(String name) {
        super("Restaurant with name " + name + " could not be found!");
    }
    public RestaurantNotFoundException(Long id) {
        super("Restaurant with ID " + id + " could not be found!");
    }
}
