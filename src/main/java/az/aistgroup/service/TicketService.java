package az.aistgroup.service;

import az.aistgroup.domain.dto.TicketDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TicketService {
    List<TicketDto> getAllTickets();

    TicketDto getTicketById(Long id);

    TicketDto addTicket(TicketDto ticketDto);

    TicketDto updateTicket(Long id, TicketDto ticketDto);

    void deleteTicket(Long id);
}
