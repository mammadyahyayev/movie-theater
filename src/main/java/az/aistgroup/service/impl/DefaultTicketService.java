package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.entity.Ticket;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.TicketRepository;
import az.aistgroup.service.TicketService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

public class DefaultTicketService implements TicketService {
    private final TicketRepository ticketRepository;

    public DefaultTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.getAllTickets();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDto getTicketById(final Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> new TicketDto())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with " + id + " not found!"));
    }

    @Override
    @Transactional
    public TicketDto addTicket(final TicketDto ticketDto) {
        Objects.requireNonNull(ticketDto, "ticketDto can not be null!");

        var ticket = new Ticket();
        Ticket newTicket = ticketRepository.save(ticket);

        //TODO: Use mapper
        return new TicketDto();
    }

    @Override
    @Transactional
    public TicketDto updateTicket(final Long id, final TicketDto ticketDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(ticketDto, "ticketDto can not be null!");

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with " + id + " not found!"));

        Ticket updatedTicket = ticketRepository.save(ticket);

        return new TicketDto();
    }

    @Override
    @Transactional
    public void deleteTicket(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        ticketRepository.findById(id)
                .ifPresentOrElse(ticketRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Ticket with " + id + " not found!");
                        });
    }
}
