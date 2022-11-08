package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoTransformer {
    public static ToDoDto convertToDto(ToDo toDo){
        List<String> collaborators = new ArrayList<>();
        if (toDo.getCollaborators()!=null){
            toDo.getCollaborators().forEach(user -> collaborators.add(user.getUsername()));
        }

        return new ToDoDto(
                toDo.getId(),
                toDo.getTitle(),
                toDo.getCreatedAt(),
                toDo.getOwner().getId(),
                collaborators);
    }

    public static ToDo convertToEntity(ToDoDto toDoDto, ToDo toDoFromDB){
        toDoFromDB.setTitle(toDoDto.getTitle());
        return toDoFromDB;
    }
}
