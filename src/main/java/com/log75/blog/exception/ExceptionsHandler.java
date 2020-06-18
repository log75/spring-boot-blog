package com.log75.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by alireza on 5/11/20.
 */
@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(UsernameNotFoundExceptions.class)
    public ResponseEntity exeption() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
    }
    /*  @ExceptionHandler(UsernameNotFoundExceptions.class)
    public ModelAndView exeption() {
        ModelAndView modelAndView = new ModelAndView("heyhey");
        modelAndView.addObject("hey", "hey");
        return modelAndView;
    }*/
}
