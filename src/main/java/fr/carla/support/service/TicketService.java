package fr.carla.support.service;

import fr.carla.support.dto.CreateTicketRequest;
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Ticket getTicketById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Ticket> getAllTickets() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}