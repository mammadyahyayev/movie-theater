package az.aistgroup.service.impl;

import az.aistgroup.repository.TicketRepository;
import az.aistgroup.service.TicketService;

public class DefaultTicketService implements TicketService {
    private final TicketRepository ticketRepository;

    public DefaultTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
}
