package fr.carla.support.dto;

import fr.carla.support.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private TicketStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}