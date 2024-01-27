package az.aistgroup.controller;

import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRefundDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.security.SecurityUtils;
import az.aistgroup.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
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
        checkUserHasPermission(ticketRequestDto.getUsername());

        TicketDto ticket = ticketService.buyTicket(ticketRequestDto);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PostMapping("/refund")
    public ResponseEntity<OperationResponseDto> refundTicket(@Valid @RequestBody TicketRefundDto ticketRefundDto) {
        checkUserHasPermission(ticketRefundDto.getUsername());

        var ticketId = ticketRefundDto.getTicketId();
        ticketService.refundTicket(ticketRefundDto);
        var response = new OperationResponseDto(true, "Ticket with id: " + ticketId + " refunded...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<TicketDto> updateTicket(
            @PathVariable("id") Long id, @Valid @RequestBody TicketRequestDto ticketRequestDto) {
        TicketDto ticket = ticketService.updateTicket(id, ticketRequestDto);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    //TODO: This method is duplicate, find a way to extract it
    private void checkUserHasPermission(String username) {
        if (SecurityUtils.isSameLoggedInUser(username)) {
            return;
        }

        throw new AccessDeniedException("You don't have permission to access the resource!");
    }
}
