package fr.carla.support.service;

import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.exception.TicketConflictException;
import fr.carla.support.exception.TicketNotFoundException;
import fr.carla.support.model.Priority;
import fr.carla.support.model.Ticket;
import fr.carla.support.model.TicketStatus;
import fr.carla.support.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldCreateTicket() {
        CreateTicketRequest request = new CreateTicketRequest("Computer issue", Priority.HIGH);

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(1L);
                    return ticket;
                });

        Ticket result = ticketService.createTicket(request);

        assertEquals(1L, result.getId());
        assertEquals("Computer issue", result.getTitle());
        assertEquals(Priority.HIGH, result.getPriority());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldCreateTicketWithOpenStatus() {
        CreateTicketRequest request = new CreateTicketRequest("Printer issue", Priority.MEDIUM);

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = ticketService.createTicket(request);

        assertEquals(TicketStatus.OPEN, result.getStatus());
    }

    @Test
    void shouldFindExistingTicket() {
        Ticket ticket = new Ticket(1L, "Network issue", Priority.HIGH, TicketStatus.OPEN);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        Ticket result = ticketService.getTicketById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Network issue", result.getTitle());
    }

    @Test
    void shouldThrowWhenTicketDoesNotExist() {
        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.getTicketById(99L)
        );
    }

    @Test
    void shouldListAllTickets() {
        when(ticketRepository.findAll())
                .thenReturn(List.of(
                        new Ticket(1L, "Issue one", Priority.LOW, TicketStatus.OPEN),
                        new Ticket(2L, "Issue two", Priority.HIGH, TicketStatus.RESOLVED)
                ));

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(2, result.size());
    }

    @Test
    void shouldAllowOpenToInProgress() {
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.OPEN);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        Ticket result = ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS);

        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void shouldAllowOpenToResolved() {
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.OPEN);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        Ticket result = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        assertEquals(TicketStatus.RESOLVED, result.getStatus());
    }

    @Test
    void shouldAllowInProgressToResolved() {
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.IN_PROGRESS);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        Ticket result = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        assertEquals(TicketStatus.RESOLVED, result.getStatus());
    }

    @Test
    void shouldRejectStatusChangeWhenTicketIsResolved() {
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.RESOLVED);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                TicketConflictException.class,
                () -> ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS)
        );

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.IN_PROGRESS);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                TicketConflictException.class,
                () -> ticketService.updateStatus(1L, TicketStatus.OPEN)
        );

        verify(ticketRepository, never()).save(any());
    }
}