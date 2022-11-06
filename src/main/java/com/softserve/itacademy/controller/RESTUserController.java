package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.ToDoDto;
import com.softserve.itacademy.dto.ToDoTransformer;
import com.softserve.itacademy.exception.EntityNotCreatedException;
import com.softserve.itacademy.exception.EntityNotUpdatedException;
import com.softserve.itacademy.exception.ErrorResponse;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RESTUserController {
    private final UserService userService;
    private final RoleService roleService;
    private final ToDoService toDoService;

    @Autowired
    public RESTUserController(UserService userService, RoleService roleService, ToDoService toDoService) {
        this.userService = userService;
        this.roleService = roleService;
        this.toDoService = toDoService;
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
    public void updateUser(@PathVariable long id, @RequestBody @Valid User user, BindingResult result) {
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

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping("/{user_id}/todos")
    public List<ToDoDto> getUsersToDos(@PathVariable long user_id) {
        userService.readById(user_id);
        List<ToDoDto> resultList = new ArrayList<>();
        List<ToDo> byUserId = toDoService.getByUserId(user_id);
        byUserId.forEach(toDo -> resultList.add(ToDoTransformer.convertToDto(toDo)));
        return resultList;
    }

    @GetMapping("/{user_id}/todos/{todo_id}")
    public ToDoDto getToDo(@PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDoDto toDoDto = ToDoTransformer.convertToDto(toDoService.readById(todo_id));
        if (toDoDto.getOwner_id() != user_id) {
            throw new EntityNotCreatedException("user is not owner");
        }
        return toDoDto;
    }

    @PutMapping("/{user_id}/todos")
    public ResponseEntity<ToDoDto> createToDo(@RequestBody @Valid ToDoDto toDoDto, @PathVariable long user_id, BindingResult result) {
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
        ToDo toDo = ToDoTransformer.convertToEntity(toDoDto, new ToDo());
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setOwner(userService.readById(user_id));
        return new ResponseEntity<>(ToDoTransformer.convertToDto(toDoService.create(toDo)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{user_id}/todos/{todo_id}")
    public void deleteToDo(@PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDoDto toDoDto = ToDoTransformer.convertToDto(toDoService.readById(todo_id));
        if (toDoDto.getOwner_id() != user_id) {
            throw new EntityNotCreatedException("user is not owner");
        }
        toDoService.delete(todo_id);
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
