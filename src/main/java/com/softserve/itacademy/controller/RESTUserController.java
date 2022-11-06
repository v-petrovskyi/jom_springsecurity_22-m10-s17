package com.softserve.itacademy.controller;

import com.softserve.itacademy.exception.EntityNotCreatedException;
import com.softserve.itacademy.exception.EntityNotUpdatedException;
import com.softserve.itacademy.exception.ErrorResponse;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class RESTUserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RESTUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User returnUser(@PathVariable long id) {
        return userService.readById(id);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errMessage = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                errMessage
                        .append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new EntityNotCreatedException(errMessage.toString());
        }
        user.setRole(roleService.readById(2));
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable long id, @RequestBody @Valid User user, BindingResult result){
        if (result.hasErrors()) {
            StringBuilder errMessage = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                errMessage
                        .append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new EntityNotUpdatedException(errMessage.toString());
        }
        User readById = userService.readById(id);
        readById.setFirstName(user.getFirstName());
        readById.setLastName(user.getLastName());
        readById.setEmail(user.getEmail());
        userService.update(readById);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EntityNotUpdatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EntityNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}