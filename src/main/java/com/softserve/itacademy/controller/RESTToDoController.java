package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.exception.EntityNotCreatedException;
import com.softserve.itacademy.exception.EntityNotUpdatedException;
import com.softserve.itacademy.exception.ErrorResponse;
import com.softserve.itacademy.exception.UserIsNotOwner;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
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
@RequestMapping("/api/todos")
public class RESTToDoController {
    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;

    @Autowired
    public RESTToDoController(TaskService taskService, ToDoService toDoService, StateService stateService) {
        this.taskService = taskService;
        this.toDoService = toDoService;
        this.stateService = stateService;
    }

    @GetMapping("/{todo_id}/tasks/{task_id}")
    public TaskDto getTask(@PathVariable long task_id, @PathVariable long todo_id){
        Task task = taskService.readById(task_id);
        if(task.getTodo().getId()!=todo_id){
            throw new EntityNotCreatedException("task not exist in this todo");
        }
        return TaskTransformer.convertToDto(task);
    }

    @PostMapping("/{todo_id}/tasks")
    public ResponseEntity<TaskDto> createTask(@PathVariable long todo_id, @RequestBody TaskDto taskDto) {
        ToDo toDo = toDoService.readById(todo_id);
        Task task = TaskTransformer.convertToEntity(taskDto, toDo, stateService.getByName("New"));
        return new ResponseEntity<>(TaskTransformer.convertToDto(taskService.create(task)), HttpStatus.CREATED);
    }

    @PutMapping("/{todo_id}/tasks/{task_id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable long todo_id, @RequestBody TaskDto taskDto, @PathVariable String task_id, BindingResult result) {
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
        ToDo toDo = toDoService.readById(todo_id);
        Task task = TaskTransformer.convertToEntity(taskDto, toDo, stateService.getByName(taskDto.getState()));
        if(task.getTodo().getId()!=todo_id){
            throw new EntityNotCreatedException("task not exist in this todo");
        }
        return new ResponseEntity<>(TaskTransformer.convertToDto(taskService.create(task)), HttpStatus.CREATED);
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
