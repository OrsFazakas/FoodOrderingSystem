package org.example.menuservice.exception.restaurant.advice;

import org.example.menuservice.exception.restaurant.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestaurantNotFoundAdvice {
    @ExceptionHandler(RestaurantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(RestaurantNotFoundException restaurantNotFoundException) {
        return restaurantNotFoundException.getMessage();
    }
}