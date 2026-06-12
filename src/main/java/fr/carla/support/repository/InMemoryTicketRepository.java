package fr.carla.support.repository;

import fr.carla.support.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final ConcurrentHashMap<Long, Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(sequence.getAndIncrement());
        }

        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(tickets.get(id));
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    @Override
    public void clear() {
        tickets.clear();
        sequence.set(1);
    }
}