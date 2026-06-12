package fr.carla.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.dto.UpdateStatusRequest;
import fr.carla.support.exception.TicketConflictException;
import fr.carla.support.exception.TicketNotFoundException;
import fr.carla.support.model.Priority;
import fr.carla.support.model.Ticket;
import fr.carla.support.model.TicketStatus;
import fr.carla.support.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCreatedWhenTicketIsCreated() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest("Computer issue", Priority.HIGH);
        Ticket ticket = new Ticket(1L, "Computer issue", Priority.HIGH, TicketStatus.OPEN);

        when(ticketService.createTicket(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Computer issue"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(null, Priority.HIGH);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTicketById() throws Exception {
        Ticket ticket = new Ticket(1L, "Computer issue", Priority.HIGH, TicketStatus.OPEN);

        when(ticketService.getTicketById(1L))
                .thenReturn(ticket);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Computer issue"));
    }

    @Test
    void shouldReturnNotFoundWhenTicketDoesNotExist() throws Exception {
        when(ticketService.getTicketById(99L))
                .thenThrow(new TicketNotFoundException(99L));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found: 99"));
    }

    @Test
    void shouldReturnAllTickets() throws Exception {
        when(ticketService.getAllTickets())
                .thenReturn(List.of(
                        new Ticket(1L, "Issue one", Priority.LOW, TicketStatus.OPEN),
                        new Ticket(2L, "Issue two", Priority.HIGH, TicketStatus.RESOLVED)
                ));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateTicketStatus() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(TicketStatus.IN_PROGRESS);
        Ticket ticket = new Ticket(1L, "Issue", Priority.MEDIUM, TicketStatus.IN_PROGRESS);

        when(ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS))
                .thenReturn(ticket);

        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturnConflictWhenBusinessRuleIsBroken() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(TicketStatus.IN_PROGRESS);

        when(ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS))
                .thenThrow(new TicketConflictException("Resolved ticket cannot change status"));

        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Resolved ticket cannot change status"));
    }
}