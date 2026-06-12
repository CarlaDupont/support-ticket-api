package fr.carla.support.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.dto.UpdateStatusRequest;
import fr.carla.support.model.Priority;
import fr.carla.support.model.TicketStatus;
import fr.carla.support.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.clear();
    }

    @Test
    void shouldCreateFindAndUpdateTicketThroughApi() throws Exception {
        CreateTicketRequest createRequest =
                new CreateTicketRequest("Computer issue", Priority.HIGH);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status").value("OPEN"));

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Computer issue"));

        UpdateStatusRequest updateRequest =
                new UpdateStatusRequest(TicketStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
}