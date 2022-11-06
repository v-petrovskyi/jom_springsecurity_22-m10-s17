package com.softserve.itacademy.controller;

import com.softserve.itacademy.exception.EntityNotFoundErrorResponse;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class RESTUserController {
    private final UserService userService;

    @Autowired
    public RESTUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User returnUser(@PathVariable long id) {
        return userService.readById(id);
    }

    @ExceptionHandler
    private ResponseEntity<EntityNotFoundErrorResponse> handleException(EntityNotFoundException e){
        EntityNotFoundErrorResponse response = new EntityNotFoundErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

//    @PostMapping("/")
//    public User createUser(@RequestBody User user, BindingResult result){
//
//    }



}
