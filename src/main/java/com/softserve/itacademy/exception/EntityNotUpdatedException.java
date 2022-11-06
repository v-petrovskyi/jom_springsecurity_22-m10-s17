package com.softserve.itacademy.exception;

public class EntityNotUpdatedException extends RuntimeException {
    public EntityNotUpdatedException() {
    }

    public EntityNotUpdatedException(String message) {
        super(message);
    }
}
