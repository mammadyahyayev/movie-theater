package az.aistgroup.controller;

import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable("id") Long id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PostMapping("/buy")
    public ResponseEntity<TicketDto> buyTicket(@Valid @RequestBody TicketRequestDto ticketRequestDto) {
        TicketDto ticket = ticketService.buyTicket(ticketRequestDto);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PostMapping("/refund/{id}")
    public ResponseEntity<OperationResponseDto> refundTicket(@PathVariable("id") Long ticketId) {
        ticketService.refundTicket(ticketId);
        var response = new OperationResponseDto(true, "Ticket with " + ticketId + " refunded...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable("id") Long id, @Valid @RequestBody TicketDto ticketDto) {
        TicketDto ticket = ticketService.updateTicket(id, ticketDto);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

}
