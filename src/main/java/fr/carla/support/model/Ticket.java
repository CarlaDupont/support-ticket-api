package fr.carla.support.model;

public class Ticket {

    private Long id;
    private String title;
    private Priority priority;
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(Long id, String title, Priority priority, TicketStatus status) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}