package org.example.menuservice.service.interfaces;

import org.example.menuservice.model.MenuItem;

import java.util.List;

public interface IMenuItemService {
    boolean checkIfMenuItemExists(String name, Float price);
    MenuItem addMenuItem(MenuItem menuItemToAdd);
    MenuItem getMenuItemById(Long id);
    List<MenuItem> searchMenuItemByName(String nameQuery);
    List<MenuItem> getAllMenuItems();
    void updateMenuItem(Long id, MenuItem menuItemToUpdate);
    void deleteMenuItem(Long id);
}
