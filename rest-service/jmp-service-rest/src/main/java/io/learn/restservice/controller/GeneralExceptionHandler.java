package io.learn.restservice.controller;

import java.util.HashMap;
import java.util.Map;

import io.learn.restservice.controller.exception.IdMismatchException;
import io.learn.restservice.exception.SubscriptionNotFoundException;
import io.learn.restservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class, SubscriptionNotFoundException.class})
    @ResponseBody
    public ResponseEntity handleUserNotFound(RuntimeException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                Map.of("error", exception.getMessage())
            );
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity handleIdsMismatch(IdMismatchException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                Map.of("error", exception.getMessage())
            );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
