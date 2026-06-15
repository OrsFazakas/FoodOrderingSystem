package org.example.menuservice.exception.menuItem;

public class MenuItemAlreadyExistsException extends RuntimeException {
    public MenuItemAlreadyExistsException(String name, Float price) {
        super("Menu item with name " + name + " and price " + price + " already exists ! Try something else instead!");
    }
}
