package fr.carla.support.controller;

import fr.carla.support.exception.TicketConflictException;
import fr.carla.support.exception.TicketNotFoundException;
import fr.carla.support.exception.TicketValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TicketValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(TicketValidationException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBeanValidation(MethodArgumentNotValidException exception) {
        return Map.of("error", "Invalid request");
    }

    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(TicketNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(TicketConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(TicketConflictException exception) {
        return Map.of("error", exception.getMessage());
    }
}