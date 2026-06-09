package org.example.menuservice.exception.menuItem.advice;

import org.example.menuservice.exception.menuItem.MenuItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MenuItemNotFoundAdvice {
    @ExceptionHandler(MenuItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(MenuItemNotFoundException restaurantNotFoundException) {
        return restaurantNotFoundException.getMessage();
    }
}