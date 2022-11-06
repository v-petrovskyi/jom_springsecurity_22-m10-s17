package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.ToDo;

public class ToDoTransformer {
    public static ToDoDto convertToDto(ToDo toDo){
        return new ToDoDto(
                toDo.getId(),
                toDo.getTitle(),
                toDo.getCreatedAt(),
                toDo.getOwner().getId());
    }

    public static ToDo convertToEntity(ToDoDto toDoDto, ToDo toDoFromDB){
        toDoFromDB.setTitle(toDoDto.getTitle());
        return toDoFromDB;
    }
}
