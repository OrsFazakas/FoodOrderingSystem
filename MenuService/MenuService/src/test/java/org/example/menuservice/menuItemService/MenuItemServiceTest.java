package org.example.menuservice.menuItemService;

import org.example.menuservice.dataAccessLayer.MenuItemRepository;
import org.example.menuservice.exception.menuItem.MenuItemAlreadyExistsException;
import org.example.menuservice.exception.menuItem.MenuItemNotFoundException;
import org.example.menuservice.model.MenuItem;
import org.example.menuservice.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private MenuItem sampleMenuItem;

    @BeforeEach
    void setUp() {
        sampleMenuItem = MenuItem.builder()
                .id(1L)
                .name("Margherita Pizza")
                .price(12.99f)
                .build();
    }


    @Test
    void addMenuItem_WhenNotExists_SavesAndReturnsMenuItem() {
        when(menuItemRepository.existsByNameAndPrice("Margherita Pizza", 12.99f)).thenReturn(false);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(sampleMenuItem);

        MenuItem result = menuItemService.addMenuItem(sampleMenuItem);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Margherita Pizza");
        assertThat(result.getPrice()).isEqualTo(12.99f);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void addMenuItem_WhenAlreadyExists_ThrowsMenuItemAlreadyExistsException() {
        when(menuItemRepository.existsByNameAndPrice("Margherita Pizza", 12.99f)).thenReturn(true);

        assertThatThrownBy(() -> menuItemService.addMenuItem(sampleMenuItem))
                .isInstanceOf(MenuItemAlreadyExistsException.class)
                .hasMessageContaining("Margherita Pizza");

        verify(menuItemRepository, never()).save(any());
    }


    @Test
    void getMenuItemById_WhenExists_ReturnsMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(sampleMenuItem));

        MenuItem result = menuItemService.getMenuItemById(1L);

        assertThat(result).isEqualTo(sampleMenuItem);
    }

    @Test
    void getMenuItemById_WhenNotExists_ThrowsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.getMenuItemById(99L))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void searchMenuItemByName_WhenFound_ReturnsList() {
        when(menuItemRepository.findAllByNameContaining("Pizza")).thenReturn(List.of(sampleMenuItem));

        List<MenuItem> result = menuItemService.searchMenuItemByName("Pizza");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Margherita Pizza");
    }

    @Test
    void searchMenuItemByName_WhenNotFound_ThrowsMenuItemNotFoundException() {
        when(menuItemRepository.findAllByNameContaining("Sushi")).thenReturn(List.of());

        assertThatThrownBy(() -> menuItemService.searchMenuItemByName("Sushi"))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("Sushi");
    }


    @Test
    void getAllMenuItems_ReturnsAllItems() {
        MenuItem second = MenuItem.builder().id(2L).name("Tiramisu").price(5.50f).build();
        when(menuItemRepository.findAll()).thenReturn(List.of(sampleMenuItem, second));

        List<MenuItem> result = menuItemService.getAllMenuItems();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllMenuItems_WhenEmpty_ReturnsEmptyList() {
        when(menuItemRepository.findAll()).thenReturn(List.of());

        List<MenuItem> result = menuItemService.getAllMenuItems();

        assertThat(result).isEmpty();
    }


    @Test
    void updateMenuItem_WhenExists_UpdatesSuccessfully() {
        MenuItem updated = MenuItem.builder().name("BBQ Pizza").price(14.99f).build();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(sampleMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(sampleMenuItem);

        menuItemService.updateMenuItem(1L, updated);

        verify(menuItemRepository).save(argThat(item ->
                item.getName().equals("BBQ Pizza") && item.getPrice().equals(14.99f)
        ));
    }

    @Test
    void updateMenuItem_WhenNotExists_ThrowsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.updateMenuItem(99L, sampleMenuItem))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).save(any());
    }


    @Test
    void deleteMenuItem_WhenExists_DeletesSuccessfully() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(sampleMenuItem));

        menuItemService.deleteMenuItem(1L);

        verify(menuItemRepository, times(1)).delete(sampleMenuItem);
    }

    @Test
    void deleteMenuItem_WhenNotExists_ThrowsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.deleteMenuItem(99L))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).delete(any());
    }


    @Test
    void checkIfMenuItemExists_WhenExists_ReturnsTrue() {
        when(menuItemRepository.existsByNameAndPrice("Margherita Pizza", 12.99f)).thenReturn(true);

        boolean result = menuItemService.checkIfMenuItemExists("Margherita Pizza", 12.99f);

        assertThat(result).isTrue();
    }

    @Test
    void checkIfMenuItemExists_WhenNotExists_ReturnsFalse() {
        when(menuItemRepository.existsByNameAndPrice("Sushi", 20.00f)).thenReturn(false);

        boolean result = menuItemService.checkIfMenuItemExists("Sushi", 20.00f);

        assertThat(result).isFalse();
    }
}