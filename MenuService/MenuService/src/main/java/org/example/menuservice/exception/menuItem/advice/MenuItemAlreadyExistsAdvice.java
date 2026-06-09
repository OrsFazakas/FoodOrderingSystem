package org.example.menuservice.exception.menuItem.advice;

import org.example.menuservice.exception.menuItem.MenuItemAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MenuItemAlreadyExistsAdvice {
    @ExceptionHandler(MenuItemAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String employeeNotFoundHandler(MenuItemAlreadyExistsException restaurantAlreadyExistsException) {
        return restaurantAlreadyExistsException.getMessage();
    }
}