package org.example.menuservice.service;

import lombok.AllArgsConstructor;
import org.example.menuservice.dataAccessLayer.RestaurantRepository;
import org.example.menuservice.exception.menuItem.MenuItemNotFoundException;
import org.example.menuservice.exception.restaurant.RestaurantAlreadyExistsException;
import org.example.menuservice.exception.restaurant.RestaurantNotFoundException;
import org.example.menuservice.model.MenuItem;
import org.example.menuservice.model.Restaurant;
import org.example.menuservice.service.interfaces.IRestaurantService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RestaurantService implements IRestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemService menuItemService;

    public Restaurant addRestaurant(Restaurant restaurantToAdd){
        String name = restaurantToAdd.getName();
        String description = restaurantToAdd.getDescription();
        String city = restaurantToAdd.getCity();

        if(checkRestaurantInCity(name, city)){
            throw new RestaurantAlreadyExistsException(name, city);
        }

        Restaurant restaurant = Restaurant.builder()
                                .name(name)
                                .description(description)
                                .city(city)
                                .menuItems(new HashSet<>())
                                .build();

        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantById(Long id){
        return restaurantRepository.findById(id).orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurantsByName(String nameQuery) {
        List<Restaurant> restaurantList = restaurantRepository.findAllByNameContaining(nameQuery);

        if(restaurantList.isEmpty()){
            throw new RestaurantNotFoundException(nameQuery);
        }

        return restaurantList;
    }

    @Override
    public List<Restaurant> filterRestaurantsByCity(String cityQuery) {
        List<Restaurant> restaurantList = restaurantRepository.findAllByCityContaining(cityQuery);

        if(restaurantList.isEmpty()){
            throw new RestaurantNotFoundException();
        }

        return restaurantList;
    }

    @Override
    public Set<MenuItem> getRestaurantMenuItems(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);

        return restaurant.getMenuItems();
    }

    public void updateRestaurant(Long id, Restaurant restaurantToUpdate){
        Restaurant existingRestaurant = getRestaurantById(id);

        existingRestaurant.setName(restaurantToUpdate.getName());
        existingRestaurant.setDescription(restaurantToUpdate.getDescription());
        existingRestaurant.setCity(restaurantToUpdate.getCity());

        restaurantRepository.save(existingRestaurant);
    }


    public void deleteRestaurant(Long id){
        Restaurant restaurantToDelete = getRestaurantById(id);

        restaurantRepository.delete(restaurantToDelete);
    }

    @Override
    public void addMenuItem(Long restaurantId, Long menuItemId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        MenuItem menuItem = menuItemService.getMenuItemById(menuItemId);

        Set<MenuItem> restaurantMenuItems =  restaurant.getMenuItems();
        restaurantMenuItems.add(menuItem);
        restaurant.setMenuItems(restaurantMenuItems);

        restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        MenuItem menuItem = menuItemService.getMenuItemById(menuItemId);

        Set<MenuItem> restaurantMenuItems =  restaurant.getMenuItems();
        if(!restaurantMenuItems.contains(menuItem)){
            throw new MenuItemNotFoundException(menuItemId);
        }

        restaurantMenuItems.remove(menuItem);
        restaurant.setMenuItems(restaurantMenuItems);

        restaurantRepository.save(restaurant);
    }

    public boolean checkRestaurantInCity(String name, String city){
        return restaurantRepository.existsByNameAndCity(name, city);
    }
}
