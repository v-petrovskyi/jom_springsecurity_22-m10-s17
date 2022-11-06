package com.softserve.itacademy.exception;

public class UserIsNotOwner extends RuntimeException {
    public UserIsNotOwner(String message) {
        super(message);
    }
}
