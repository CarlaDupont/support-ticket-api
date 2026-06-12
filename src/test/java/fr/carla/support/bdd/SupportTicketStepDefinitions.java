package fr.carla.support.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.carla.support.dto.CreateTicketRequest;
import fr.carla.support.dto.UpdateStatusRequest;
import fr.carla.support.model.Priority;
import fr.carla.support.model.TicketStatus;
import fr.carla.support.repository.TicketRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class SupportTicketStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    private MvcResult lastResult;
    private Long currentTicketId;

    @Before
    public void setUp() {
        ticketRepository.clear();
        lastResult = null;
        currentTicketId = null;
    }

    @Given("the ticket system is empty")
    public void theTicketSystemIsEmpty() {
        ticketRepository.clear();
    }

    @Given("an open support ticket exists with title {string} and priority {string}")
    public void anOpenSupportTicketExists(String title, String priority) throws Exception {
        CreateTicketRequest request =
                new CreateTicketRequest(title, Priority.valueOf(priority));

        lastResult = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        currentTicketId = objectMapper
                .readTree(lastResult.getResponse().getContentAsString())
                .get("id")
                .asLong();
    }

    @Given("a resolved support ticket exists with title {string} and priority {string}")
    public void aResolvedSupportTicketExists(String title, String priority) throws Exception {
        anOpenSupportTicketExists(title, priority);

        UpdateStatusRequest request =
                new UpdateStatusRequest(TicketStatus.RESOLVED);

        lastResult = mockMvc.perform(patch("/api/tickets/" + currentTicketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @When("the user creates a support ticket with title {string} and priority {string}")
    public void theUserCreatesASupportTicket(String title, String priority) throws Exception {
        CreateTicketRequest request =
                new CreateTicketRequest(title, Priority.valueOf(priority));

        lastResult = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (lastResult.getResponse().getStatus() == 201) {
            currentTicketId = objectMapper
                    .readTree(lastResult.getResponse().getContentAsString())
                    .get("id")
                    .asLong();
        }
    }

    @When("the user changes the ticket status to {string}")
    public void theUserChangesTheTicketStatusTo(String status) throws Exception {
        UpdateStatusRequest request =
                new UpdateStatusRequest(TicketStatus.valueOf(status));

        lastResult = mockMvc.perform(patch("/api/tickets/" + currentTicketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @When("the user requests ticket with id {long}")
    public void theUserRequestsTicketWithId(Long id) throws Exception {
        lastResult = mockMvc.perform(get("/api/tickets/" + id))
                .andReturn();
    }

    @Then("the ticket is created")
    public void theTicketIsCreated() {
        assertEquals(201, lastResult.getResponse().getStatus());
    }

    @Then("the ticket status is {string}")
    public void theTicketStatusIs(String expectedStatus) throws Exception {
        String content = lastResult.getResponse().getContentAsString();

        String actualStatus = objectMapper
                .readTree(content)
                .get("status")
                .asText();

        assertEquals(expectedStatus, actualStatus);
    }

    @Then("the ticket status is updated to {string}")
    public void theTicketStatusIsUpdatedTo(String expectedStatus) throws Exception {
        assertEquals(200, lastResult.getResponse().getStatus());
        theTicketStatusIs(expectedStatus);
    }

    @Then("a conflict error is returned")
    public void aConflictErrorIsReturned() {
        assertEquals(409, lastResult.getResponse().getStatus());
    }

    @Then("a not found error is returned")
    public void aNotFoundErrorIsReturned() {
        assertEquals(404, lastResult.getResponse().getStatus());
    }
}