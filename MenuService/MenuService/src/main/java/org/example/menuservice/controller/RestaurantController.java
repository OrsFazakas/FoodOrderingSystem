package org.example.menuservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menuservice.dataTransferObjects.menuItem.mapper.MenuItemMapper;
import org.example.menuservice.dataTransferObjects.menuItem.out.MenuItemResponseDTO;
import org.example.menuservice.dataTransferObjects.restaurant.in.RestaurantCreationDTO;
import org.example.menuservice.dataTransferObjects.restaurant.mapper.RestaurantMapper;
import org.example.menuservice.dataTransferObjects.restaurant.out.RestaurantResponseDTO;
import org.example.menuservice.model.MenuItem;
import org.example.menuservice.model.Restaurant;
import org.example.menuservice.service.interfaces.IRestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final IRestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long id){
        return ResponseEntity.ok(restaurantMapper.toResponseDTO(restaurantService.getRestaurantById(id)));
    }

    @GetMapping()
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants(){
        List<RestaurantResponseDTO> restaurantList = restaurantService.getAllRestaurants()
                .stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/name")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByName(@RequestParam String nameQuery){
        List<RestaurantResponseDTO> restaurantList = restaurantService.searchRestaurantsByName(nameQuery)
                .stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/city")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByCity(@RequestParam String cityQuery){
        List<RestaurantResponseDTO> restaurantList = restaurantService.filterRestaurantsByCity(cityQuery)
                .stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/{id}/menuItems")
    public ResponseEntity<Set<MenuItemResponseDTO>> getRestaurantMenuItems(@PathVariable Long id){
        Set<MenuItemResponseDTO> menuItemSet = restaurantService.getRestaurantMenuItems(id)
                .stream()
                .map(menuItemMapper::toResponseDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(menuItemSet);
    }

    @PostMapping() // ADMIN ONLY
    public ResponseEntity<RestaurantResponseDTO> addRestaurant(@Valid @RequestBody RestaurantCreationDTO restaurantCreationDTO){
        RestaurantResponseDTO restaurantResponseDTO = restaurantMapper.toResponseDTO(
                                                            restaurantService.addRestaurant(
                                                                restaurantMapper.toRestaurant(restaurantCreationDTO)
                                                            )
                                                        );

        return ResponseEntity.ok(restaurantResponseDTO);
    }

    @PutMapping("/{id}") // ADMIN ONLY
    public ResponseEntity<String> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantCreationDTO restaurantCreationDTO){
        restaurantService.updateRestaurant(id, restaurantMapper.toRestaurant(restaurantCreationDTO));

        return ResponseEntity.ok("Restaurant updated successfully!");
    }

    @DeleteMapping("/{id}") // ADMIN ONLY
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id){
        restaurantService.deleteRestaurant(id);

        return ResponseEntity.ok("Restaurant deleted successfully!");
    }

    @PostMapping("/{id}/menuItems") // ADMIN ONLY
    public ResponseEntity<String> addRestaurantMenuItem(@PathVariable Long id, @RequestParam Long menuItemId){
        restaurantService.addMenuItem(id, menuItemId);

        return ResponseEntity.ok("Menu item successfully added to restaurant!");
    }

    @DeleteMapping("/{id}/menuItems") // ADMIN ONLY
    public ResponseEntity<String> deleteRestaurantMenuItem(@PathVariable Long id, @RequestParam Long menuItemId){
        restaurantService.deleteMenuItem(id, menuItemId);

        return ResponseEntity.ok("Menu item successfully deleted from restaurant!");
    }
}
