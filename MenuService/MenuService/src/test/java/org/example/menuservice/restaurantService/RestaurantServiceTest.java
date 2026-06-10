package org.example.menuservice.restaurantService;

import org.example.menuservice.dataAccessLayer.RestaurantRepository;
import org.example.menuservice.exception.menuItem.MenuItemNotFoundException;
import org.example.menuservice.exception.restaurant.RestaurantAlreadyExistsException;
import org.example.menuservice.exception.restaurant.RestaurantNotFoundException;
import org.example.menuservice.model.MenuItem;
import org.example.menuservice.model.Restaurant;
import org.example.menuservice.service.MenuItemService;
import org.example.menuservice.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant sampleRestaurant;
    private MenuItem sampleMenuItem;

    @BeforeEach
    void setUp() {
        sampleMenuItem = MenuItem.builder()
                .id(1L)
                .name("Margherita Pizza")
                .price(12.99f)
                .build();

        sampleRestaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .description("Best pizza in town")
                .city("Cluj-Napoca")
                .menuItems(new HashSet<>())
                .build();
    }


    @Test
    void addRestaurant_WhenNotExists_SavesAndReturnsRestaurant() {
        when(restaurantRepository.existsByNameAndCity("Pizza Palace", "Cluj-Napoca")).thenReturn(false);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(sampleRestaurant);

        Restaurant result = restaurantService.addRestaurant(sampleRestaurant);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pizza Palace");
        assertThat(result.getCity()).isEqualTo("Cluj-Napoca");
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void addRestaurant_WhenAlreadyExistsInCity_ThrowsRestaurantAlreadyExistsException() {
        when(restaurantRepository.existsByNameAndCity("Pizza Palace", "Cluj-Napoca")).thenReturn(true);

        assertThatThrownBy(() -> restaurantService.addRestaurant(sampleRestaurant))
                .isInstanceOf(RestaurantAlreadyExistsException.class)
                .hasMessageContaining("Pizza Palace")
                .hasMessageContaining("Cluj-Napoca");

        verify(restaurantRepository, never()).save(any());
    }


    @Test
    void getRestaurantById_WhenExists_ReturnsRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));

        Restaurant result = restaurantService.getRestaurantById(1L);

        assertThat(result).isEqualTo(sampleRestaurant);
    }

    @Test
    void getRestaurantById_WhenNotExists_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantById(99L))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void getAllRestaurants_ReturnsList() {
        Restaurant second = Restaurant.builder().id(2L).name("Burger Barn").city("Bucharest").menuItems(new HashSet<>()).build();
        when(restaurantRepository.findAll()).thenReturn(List.of(sampleRestaurant, second));

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllRestaurants_WhenEmpty_ReturnsEmptyList() {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertThat(result).isEmpty();
    }


    @Test
    void searchRestaurantsByName_WhenFound_ReturnsList() {
        when(restaurantRepository.findAllByNameContaining("Pizza")).thenReturn(List.of(sampleRestaurant));

        List<Restaurant> result = restaurantService.searchRestaurantsByName("Pizza");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pizza Palace");
    }

    @Test
    void searchRestaurantsByName_WhenNotFound_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findAllByNameContaining("XYZ")).thenReturn(List.of());

        assertThatThrownBy(() -> restaurantService.searchRestaurantsByName("XYZ"))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining("XYZ");
    }


    @Test
    void filterRestaurantsByCity_WhenFound_ReturnsList() {
        when(restaurantRepository.findAllByCityContaining("Cluj")).thenReturn(List.of(sampleRestaurant));

        List<Restaurant> result = restaurantService.filterRestaurantsByCity("Cluj");

        assertThat(result).hasSize(1);
    }

    @Test
    void filterRestaurantsByCity_WhenNotFound_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findAllByCityContaining("Mars")).thenReturn(List.of());

        assertThatThrownBy(() -> restaurantService.filterRestaurantsByCity("Mars"))
                .isInstanceOf(RestaurantNotFoundException.class);
    }


    @Test
    void getRestaurantMenuItems_ReturnsMenuItemSet() {
        sampleRestaurant.setMenuItems(Set.of(sampleMenuItem));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));

        Set<MenuItem> result = restaurantService.getRestaurantMenuItems(1L);

        assertThat(result).containsExactly(sampleMenuItem);
    }

    @Test
    void getRestaurantMenuItems_WhenRestaurantNotFound_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantMenuItems(99L))
                .isInstanceOf(RestaurantNotFoundException.class);
    }


    @Test
    void updateRestaurant_WhenExists_UpdatesSuccessfully() {
        Restaurant updated = Restaurant.builder().name("New Palace").description("Updated").city("Timisoara").build();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));

        restaurantService.updateRestaurant(1L, updated);

        verify(restaurantRepository).save(argThat(r ->
                r.getName().equals("New Palace") &&
                        r.getCity().equals("Timisoara") &&
                        r.getDescription().equals("Updated")
        ));
    }

    @Test
    void updateRestaurant_WhenNotExists_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.updateRestaurant(99L, sampleRestaurant))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void deleteRestaurant_WhenExists_DeletesSuccessfully() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));

        restaurantService.deleteRestaurant(1L);

        verify(restaurantRepository, times(1)).delete(sampleRestaurant);
    }

    @Test
    void deleteRestaurant_WhenNotExists_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.deleteRestaurant(99L))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(restaurantRepository, never()).delete(any());
    }


    @Test
    void addMenuItem_AddsItemToRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemService.getMenuItemById(1L)).thenReturn(sampleMenuItem);

        restaurantService.addMenuItem(1L, 1L);

        verify(restaurantRepository).save(argThat(r -> r.getMenuItems().contains(sampleMenuItem)));
    }

    @Test
    void addMenuItem_WhenRestaurantNotFound_ThrowsRestaurantNotFoundException() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.addMenuItem(99L, 1L))
                .isInstanceOf(RestaurantNotFoundException.class);
    }

    @Test
    void addMenuItem_WhenMenuItemNotFound_ThrowsMenuItemNotFoundException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemService.getMenuItemById(99L)).thenThrow(new MenuItemNotFoundException(99L));

        assertThatThrownBy(() -> restaurantService.addMenuItem(1L, 99L))
                .isInstanceOf(MenuItemNotFoundException.class);
    }


    @Test
    void deleteMenuItem_WhenItemInRestaurant_RemovesItem() {
        sampleRestaurant.setMenuItems(new HashSet<>(Set.of(sampleMenuItem)));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemService.getMenuItemById(1L)).thenReturn(sampleMenuItem);

        restaurantService.deleteMenuItem(1L, 1L);

        verify(restaurantRepository).save(argThat(r -> !r.getMenuItems().contains(sampleMenuItem)));
    }

    @Test
    void deleteMenuItem_WhenItemNotInRestaurant_ThrowsMenuItemNotFoundException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemService.getMenuItemById(1L)).thenReturn(sampleMenuItem);

        assertThatThrownBy(() -> restaurantService.deleteMenuItem(1L, 1L))
                .isInstanceOf(MenuItemNotFoundException.class);
    }


    @Test
    void checkRestaurantInCity_WhenExists_ReturnsTrue() {
        when(restaurantRepository.existsByNameAndCity("Pizza Palace", "Cluj-Napoca")).thenReturn(true);

        boolean result = restaurantService.checkRestaurantInCity("Pizza Palace", "Cluj-Napoca");

        assertThat(result).isTrue();
    }

    @Test
    void checkRestaurantInCity_WhenNotExists_ReturnsFalse() {
        when(restaurantRepository.existsByNameAndCity("Ghost Kitchen", "Berlin")).thenReturn(false);

        boolean result = restaurantService.checkRestaurantInCity("Ghost Kitchen", "Berlin");

        assertThat(result).isFalse();
    }
}