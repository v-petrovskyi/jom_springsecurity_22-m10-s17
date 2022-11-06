package com.softserve.itacademy.exception;

public class EntityNotCreatedException extends RuntimeException {

    public EntityNotCreatedException(String message) {
        super(message);
    }

    public EntityNotCreatedException() {
    }
}
