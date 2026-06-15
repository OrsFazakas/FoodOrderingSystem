package org.example.menuservice.dataTransferObjects.menuItem.mapper;

import org.example.menuservice.dataTransferObjects.menuItem.in.MenuItemCreationDTO;
import org.example.menuservice.dataTransferObjects.menuItem.out.MenuItemResponseDTO;
import org.example.menuservice.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public MenuItem toMenuItem(MenuItemCreationDTO menuItemCreationDto){
        return MenuItem.builder()
                .name(menuItemCreationDto.name())
                .price(menuItemCreationDto.price())
                .build();
    }

    public MenuItemResponseDTO toResponseDTO(MenuItem menuItem){
        return new MenuItemResponseDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getPrice()
        );
    }
}
