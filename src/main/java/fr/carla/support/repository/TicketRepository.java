package fr.carla.support.repository;

import fr.carla.support.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll();

    void clear();
}