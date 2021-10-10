package io.learn.restservice.controller.exception;

public class IdMismatchException extends RuntimeException {

    public IdMismatchException(String message) {
        super(message);
    }
}
