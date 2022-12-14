package com.softserve.itacademy.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ToDoDto {
    private long id;

    @NotBlank(message = "The 'title' cannot be empty")
    private String title;

    private LocalDateTime created_at;

    private long owner_id;

    private List<String> collaborators;

    public ToDoDto(long id, String title, LocalDateTime created_at, long owner_id, List<String> collaborators) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
        this.owner_id = owner_id;
        this.collaborators = collaborators;
    }

    public ToDoDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public List<String> getCollaborators() {
        if (collaborators == null){
            collaborators = new ArrayList<>();
        }
            return collaborators;
    }

    public void setCollaborators(List<String> collaborators) {
        this.collaborators = collaborators;
    }
}
