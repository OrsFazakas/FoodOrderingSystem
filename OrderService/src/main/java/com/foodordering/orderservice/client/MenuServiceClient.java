package com.foodordering.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", url = "${menu.service.url:http://localhost:8082}")
public interface MenuServiceClient {

    @GetMapping("/menuItems/{id}")
    MenuItemResponse getMenuItemById(@PathVariable("id") Long id);
}