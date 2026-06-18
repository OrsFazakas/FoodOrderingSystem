package com.foodordering.orderservice.client;

import com.foodordering.orderservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service", url = "http://user-service:8081", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/users/{id}/exists")
    Map<String, Boolean> checkIfUserExists(@PathVariable("id") Long id);
}