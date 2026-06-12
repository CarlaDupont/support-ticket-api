package fr.carla.support.dto;

import fr.carla.support.model.Priority;
import jakarta.validation.constraints.NotNull;

public class CreateTicketRequest {

    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Priority is required")
    private Priority priority;

    public CreateTicketRequest() {
    }

    public CreateTicketRequest(String title, Priority priority) {
        this.title = title;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}