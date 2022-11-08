package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.*;
import com.softserve.itacademy.exception.*;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.security.JwtTokenProvider;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import com.softserve.itacademy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api/users")
public class RESTUserController {
    private final UserService userService;
    private final RoleService roleService;
    private final ToDoService toDoService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    @Autowired
    public RESTUserController(UserService userService, AuthenticationManager authenticationManager, RoleService roleService, ToDoService toDoService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
        this.toDoService = toDoService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByEmail(username);
            if (user == null) {
                throw new UsernameNotFoundException("user not found");
            }
            String token = jwtTokenProvider.createToken(username, user.getRole());
            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("invalid username or password");
        }

    }

    @GetMapping("/api/users")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/api/users/{id}")
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

    @PutMapping("/api/users/{id}")
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

    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping("/api/users/{user_id}/todos")
    public List<ToDoDto> getUsersToDos(@PathVariable long user_id) {
        userService.readById(user_id);
        List<ToDoDto> resultList = new ArrayList<>();
        List<ToDo> byUserId = toDoService.getByUserId(user_id);
        byUserId.forEach(toDo -> resultList.add(ToDoTransformer.convertToDto(toDo)));
        return resultList;
    }

    @GetMapping("/api/users/{user_id}/todos/{todo_id}")
    public ToDoDto getToDo(@PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDoDto toDoDto = ToDoTransformer.convertToDto(toDoService.readById(todo_id));
        if (toDoDto.getOwner_id() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
        return toDoDto;
    }

    @PostMapping("/api/users/{user_id}/todos")
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

    @PutMapping("/api/users/{user_id}/todos/{todo_id}")
    public ResponseEntity<ToDoDto> updateToDo(@RequestBody @Valid ToDoDto toDoDto, @PathVariable long user_id, BindingResult result, @PathVariable long todo_id) {
        ToDo toDo = toDoService.readById(todo_id);
        if (toDo.getOwner().getId() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
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
        ToDo updatedToDo = ToDoTransformer.convertToEntity(toDoDto, toDo);
        updatedToDo.setOwner(userService.readById(user_id));
        updatedToDo.setTasks(toDo.getTasks());
        updatedToDo.setCollaborators(toDo.getCollaborators());
        return new ResponseEntity<>(ToDoTransformer.convertToDto(toDoService.update(updatedToDo)), HttpStatus.OK);
    }


    @DeleteMapping("/api/users/{user_id}/todos/{todo_id}")
    public void deleteToDo(@PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDo toDo = toDoService.readById(todo_id);
        if (toDo.getOwner().getId() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
        toDoService.delete(todo_id);
    }

    @GetMapping("/api/users/{user_id}/todos/{todo_id}/tasks")
    public List<TaskDto> getAllTaskFromToDo(@PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDo toDo = toDoService.readById(todo_id);
        if (toDo.getOwner().getId() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
        List<TaskDto> resultList = new ArrayList<>();
        toDo.getTasks().forEach(task -> resultList.add(TaskTransformer.convertToDto(task)));
        return resultList;
    }

    // Collaborators

    @PostMapping("/api/users/{user_id}/todos/{todo_id}/collaborators")
    public ResponseEntity<HttpStatus> addCollaborator(@RequestBody CollaboratorDto collaboratorDto, @PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDo toDo = toDoService.readById(todo_id);
        if (toDo.getOwner().getId() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
        if (collaboratorDto.getCollaborator_id() == toDo.getOwner().getId()) {
            throw new ConflictException409("user is owner");
        }
        User user = userService.readById(collaboratorDto.getCollaborator_id());
        if (toDo.getCollaborators().contains(user)) {
            throw new ConflictException409("the user is already on the list of collaborators for this todo");
        }
        if (toDo.getCollaborators().add(user)) {
            toDoService.update(toDo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/api/users/{user_id}/todos/{todo_id}/collaborators")
    public ResponseEntity<HttpStatus> removeCollaborator(@RequestBody CollaboratorDto collaboratorDto, @PathVariable long todo_id, @PathVariable long user_id) {
        userService.readById(user_id);
        ToDo toDo = toDoService.readById(todo_id);
        if (toDo.getOwner().getId() != user_id) {
            throw new UserIsNotOwner("user is not owner");
        }
        if (collaboratorDto.getCollaborator_id() == toDo.getOwner().getId()) {
            throw new ConflictException409("user is owner");
        }
        User user = userService.readById(collaboratorDto.getCollaborator_id());
        if (!toDo.getCollaborators().contains(user)) {
            throw new EntityNotFoundException("user is not collaborator");
        }
        if (toDo.getCollaborators().remove(user)) {
            toDoService.update(toDo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ConflictException409 e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
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
    private ResponseEntity<ErrorResponse> handleException(UserIsNotOwner e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
