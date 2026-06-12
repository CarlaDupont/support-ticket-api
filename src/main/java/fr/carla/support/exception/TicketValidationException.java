package fr.carla.support.exception;

public class TicketValidationException extends RuntimeException {

    public TicketValidationException(String message) {
        super(message);
    }
}