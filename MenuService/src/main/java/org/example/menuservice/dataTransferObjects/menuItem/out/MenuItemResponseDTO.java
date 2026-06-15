package org.example.menuservice.dataTransferObjects.menuItem.out;

public record MenuItemResponseDTO(
        Long id,
        String name,
        Float price
) {}
