package org.example.menuservice.service.interfaces;

import org.example.menuservice.model.MenuItem;
import org.example.menuservice.model.Restaurant;

import java.util.List;
import java.util.Set;

public interface IRestaurantService {
    boolean checkRestaurantInCity(String name, String city);
    Restaurant addRestaurant(Restaurant restaurantToAdd);
    Restaurant getRestaurantById(Long id);
    List<Restaurant> getAllRestaurants();
    List<Restaurant> searchRestaurantsByName(String nameQuery);
    List<Restaurant> filterRestaurantsByCity(String cityQuery);
    Set<MenuItem> getRestaurantMenuItems(Long restaurantId);
    void updateRestaurant(Long id, Restaurant restaurantToUpdate);
    void deleteRestaurant(Long id);
    void addMenuItem(Long restaurantId, Long menuItemId);
    void deleteMenuItem(Long restaurantId, Long menuItemId);
}
