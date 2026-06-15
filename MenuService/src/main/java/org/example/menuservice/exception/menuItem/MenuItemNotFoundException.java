package org.example.menuservice.exception.menuItem;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String name) {
        super("Menu item with name " + name + " could not be found!");
    }
    public MenuItemNotFoundException(Long id) {
        super("Menu item with ID " + id + " could not be found!");
    }
}
