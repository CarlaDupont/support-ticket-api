package fr.carla.support.controller;

import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.dto.UpdateStatusRequest;
import fr.carla.support.model.Ticket;
import fr.carla.support.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @PatchMapping("/{id}/status")
    public Ticket updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return ticketService.updateStatus(id, request.getStatus());
    }
}