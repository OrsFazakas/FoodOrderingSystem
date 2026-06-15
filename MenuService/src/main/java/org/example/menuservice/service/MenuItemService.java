package org.example.menuservice.service;

import lombok.AllArgsConstructor;
import org.example.menuservice.dataAccessLayer.MenuItemRepository;
import org.example.menuservice.exception.menuItem.MenuItemAlreadyExistsException;
import org.example.menuservice.exception.menuItem.MenuItemNotFoundException;
import org.example.menuservice.model.MenuItem;
import org.example.menuservice.service.interfaces.IMenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MenuItemService implements IMenuItemService {
    private final MenuItemRepository menuItemRepository;

    @Override
    public boolean checkIfMenuItemExists(String name, Float price) {
        return menuItemRepository.existsByNameAndPrice(name, price);
    }

    @Override
    public MenuItem addMenuItem(MenuItem menuItemToAdd) {
        String name = menuItemToAdd.getName();
        Float price = menuItemToAdd.getPrice();

        if(checkIfMenuItemExists(name, price)){
          throw new MenuItemAlreadyExistsException(name, price);
        }

        MenuItem menuItem = MenuItem.builder()
                            .name(name)
                            .price(price)
                            .build();

        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() -> new MenuItemNotFoundException(id));
    }

    @Override
    public List<MenuItem> searchMenuItemByName(String nameQuery) {
        List<MenuItem> menuItemList = menuItemRepository.findAllByNameContaining(nameQuery);

        if(menuItemList.isEmpty()){
            throw new MenuItemNotFoundException(nameQuery);
        }

        return menuItemList;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public void updateMenuItem(Long id, MenuItem menuItemToUpdate) {
        MenuItem existingMenuItem = getMenuItemById(id);

        existingMenuItem.setName(menuItemToUpdate.getName());
        existingMenuItem.setPrice(menuItemToUpdate.getPrice());

        menuItemRepository.save(existingMenuItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem menuItemToDelete = getMenuItemById(id);

        menuItemRepository.delete(menuItemToDelete);
    }
}
