package org.example.menuservice.exception.restaurant;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String name, String city) {
        super("Restaurant with name " + name + " already exists in " + city +
                "! To avoid customer confusion, try something else instead!");
    }
}
