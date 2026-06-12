package fr.carla.support.service;

import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.exception.TicketConflictException;
import fr.carla.support.exception.TicketNotFoundException;
import fr.carla.support.exception.TicketValidationException;
import fr.carla.support.model.Ticket;
import fr.carla.support.model.TicketStatus;
import fr.carla.support.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(CreateTicketRequest request) {
        validateCreationRequest(request);

        Ticket ticket = new Ticket(
                null,
                request.getTitle().trim(),
                request.getPriority(),
                TicketStatus.OPEN
        );

        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository
                .findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getTicketById(id);

        if (newStatus == null) {
            throw new TicketValidationException("Status is required");
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new TicketConflictException(
                    "Resolved ticket cannot change status"
            );
        }

        if (!isTransitionAllowed(ticket.getStatus(), newStatus)) {
            throw new TicketConflictException(
                    "Invalid status transition from "
                            + ticket.getStatus()
                            + " to "
                            + newStatus
            );
        }

        ticket.setStatus(newStatus);

        return ticketRepository.save(ticket);
    }

    private void validateCreationRequest(CreateTicketRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().length() < 3) {
            throw new TicketValidationException(
                    "Title must contain at least 3 useful characters"
            );
        }

        if (request.getPriority() == null) {
            throw new TicketValidationException("Priority is required");
        }
    }

    private boolean isTransitionAllowed(
            TicketStatus currentStatus,
            TicketStatus newStatus
    ) {
        if (currentStatus == TicketStatus.OPEN) {
            return newStatus == TicketStatus.IN_PROGRESS
                    || newStatus == TicketStatus.RESOLVED;
        }

        if (currentStatus == TicketStatus.IN_PROGRESS) {
            return newStatus == TicketStatus.RESOLVED;
        }

        return false;
    }
}