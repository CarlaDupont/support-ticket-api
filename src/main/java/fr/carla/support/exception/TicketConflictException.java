package fr.carla.support.exception;

public class TicketConflictException extends RuntimeException {

    public TicketConflictException(String message) {
        super(message);
    }
}