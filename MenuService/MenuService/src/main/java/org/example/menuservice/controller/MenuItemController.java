package org.example.menuservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menuservice.dataTransferObjects.menuItem.in.MenuItemCreationDTO;
import org.example.menuservice.dataTransferObjects.menuItem.mapper.MenuItemMapper;
import org.example.menuservice.dataTransferObjects.menuItem.out.MenuItemResponseDTO;
import org.example.menuservice.service.interfaces.IMenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menuItems")
@RequiredArgsConstructor
public class MenuItemController {
    private final IMenuItemService menuItemService;
    private final MenuItemMapper menuItemMapper;

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemById(@PathVariable Long id){
        return ResponseEntity.ok(menuItemMapper.toResponseDTO(menuItemService.getMenuItemById(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MenuItemResponseDTO>> getAllMenuItems(){
        List<MenuItemResponseDTO> menuItemList = menuItemService.getAllMenuItems()
                                                .stream()
                                                .map(menuItemMapper::toResponseDTO)
                                                .toList();

        return ResponseEntity.ok(menuItemList);
    }

    @GetMapping()
    public ResponseEntity<List<MenuItemResponseDTO>> searchMenuItems(@RequestParam String searchQuery){
        List<MenuItemResponseDTO> menuItemList = menuItemService.searchMenuItemByName(searchQuery)
                .stream()
                .map(menuItemMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(menuItemList);
    }

    @PostMapping() // ADMIN ONLY
    public ResponseEntity<MenuItemResponseDTO> addMenuItem(@Valid @RequestBody MenuItemCreationDTO menuItemCreationDTO){
        MenuItemResponseDTO menuItemResponseDTO = menuItemMapper.toResponseDTO(
                                                        menuItemService.addMenuItem(
                                                                menuItemMapper.toMenuItem(menuItemCreationDTO)
                                                        )
                                                    );

        return ResponseEntity.ok(menuItemResponseDTO);
    }

    @PutMapping("/{id}") // ADMIN ONLY
    public ResponseEntity<String> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemCreationDTO menuItemCreationDTO){
        menuItemService.updateMenuItem(id, menuItemMapper.toMenuItem(menuItemCreationDTO));

        return ResponseEntity.ok("Menu item updated successfully!");
    }

    @DeleteMapping("/{id}") // ADMIN ONLY
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id){
        menuItemService.deleteMenuItem(id);

        return ResponseEntity.ok("Menu item updated successfully!");
    }

}
