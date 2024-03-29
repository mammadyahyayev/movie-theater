package az.aistgroup.service;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRefundDto;
import az.aistgroup.domain.dto.TicketRequestDto;

import java.util.List;

public interface TicketService {
    List<TicketDto> getAllTickets();

    TicketDto getTicketById(Long id, String username);

    TicketDto buyTicket(TicketRequestDto ticketDto);

    TicketDto updateTicket(Long id, TicketRequestDto ticketRequestDto);

    void refundTicket(TicketRefundDto ticketRefundDto);
}
