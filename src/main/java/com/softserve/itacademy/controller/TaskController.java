package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.UserDetailsServiceImpl;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final UserRepository userRepository;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/create/todos/{todo_id}")
//    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
//    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
        taskService.create(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
//    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "update-task";
    }

//    @PostMapping("/{task_id}/update/todos/{todo_id}")
////    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
//    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
//                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
//        if (result.hasErrors()) {
//            model.addAttribute("priorities", Priority.values());
//            model.addAttribute("states", stateService.getAll());
//            return "update-task";
//        }
//        Task task = TaskTransformer.convertToEntity(
//                taskDto,
//                todoService.readById(taskDto.getTodoId()),
//                stateService.readById(taskDto.getState())// todo не працює бо я змінив TaskDTO
//        );
//        taskService.update(task);
//        return "redirect:/todos/" + todoId + "/tasks";
//    }

    @GetMapping("/{task_id}/delete/todos/{todo_id}")
//    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }
}
