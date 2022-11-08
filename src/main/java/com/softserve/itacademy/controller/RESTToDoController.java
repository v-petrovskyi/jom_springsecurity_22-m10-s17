package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.exception.EntityNotCreatedException;
import com.softserve.itacademy.exception.EntityNotUpdatedException;
import com.softserve.itacademy.exception.ErrorResponse;
import com.softserve.itacademy.exception.UserIsNotOwner;
import com.softserve.itacademy.model.Priority;
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
import java.util.List;

//@RestController
//@RequestMapping("/api/todos")
public class RESTToDoController {


//    @Autowired
//    public RESTToDoController(TaskService taskService, ToDoService toDoService, StateService stateService) {
//        this.taskService = taskService;
//        this.toDoService = toDoService;
//        this.stateService = stateService;
//    }

//
//    @ExceptionHandler
//    private ResponseEntity<ErrorResponse> handleException(EntityNotUpdatedException e) {
//        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler
//    private ResponseEntity<ErrorResponse> handleException(EntityNotCreatedException e) {
//        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler
//    private ResponseEntity<ErrorResponse> handleException(UserIsNotOwner e) {
//        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
//
//
//    @ExceptionHandler
//    private ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
//        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }

}
