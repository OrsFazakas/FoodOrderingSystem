package com.foodordering.orderservice.client;

import com.foodordering.orderservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", url = "${menu.service.url:http://menu-service:8082}", configuration = FeignConfig.class)
public interface MenuServiceClient {

    @GetMapping("/menuItems/{id}")
    MenuItemResponse getMenuItemById(@PathVariable("id") Long id);
}