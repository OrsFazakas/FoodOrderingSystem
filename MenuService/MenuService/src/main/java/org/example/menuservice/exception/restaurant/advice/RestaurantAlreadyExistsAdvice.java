package org.example.menuservice.exception.restaurant.advice;

import org.example.menuservice.exception.restaurant.RestaurantAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestaurantAlreadyExistsAdvice {
    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String employeeNotFoundHandler(RestaurantAlreadyExistsException restaurantAlreadyExistsException) {
        return restaurantAlreadyExistsException.getMessage();
    }
}