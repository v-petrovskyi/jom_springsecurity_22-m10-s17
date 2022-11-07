package com.softserve.itacademy.dto;

public class CollaboratorDto {
    private long collaborator_id;

    public CollaboratorDto() {
    }

    public CollaboratorDto(long collaborator_id) {
        this.collaborator_id = collaborator_id;
    }

    public long getCollaborator_id() {
        return collaborator_id;
    }

    public void setCollaborator_id(long collaborator_id) {
        this.collaborator_id = collaborator_id;
    }
}
