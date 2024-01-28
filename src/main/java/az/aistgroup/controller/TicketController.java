package az.aistgroup.controller;

import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRefundDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.exception.ErrorResponse;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static az.aistgroup.security.SecurityUtils.checkUserHasPermission;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Fetches all tickets. Only ADMIN can get all available tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all available tickets.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class))})
    })
    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @Operation(summary = "Fetch a ticket by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a ticket for given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns when ticket isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns when ticket owner's username is not the same as given username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/{id}/user/{username}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable("id") Long id, @PathVariable("username") String username) {
        checkUserHasPermission(username);

        TicketDto ticket = ticketService.getTicketById(id, username);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @Operation(summary = "Buys a ticket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a bought ticket.",
                    content = {@Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = TicketDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns when session or seat isn't found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns when session is closed or there are no tickets available to buy",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns insufficient.funds when there is not enough money in user's balance to buy.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/buy")
    public ResponseEntity<TicketDto> buyTicket(@Valid @RequestBody TicketRequestDto ticketRequestDto) {
        String username = ticketRequestDto.getUsername();
        checkUserHasPermission(username);

        TicketDto ticketDto = ticketService.buyTicket(ticketRequestDto);
        ticketDto.add(linkTo(methodOn(TicketController.class).getTicketById(ticketDto.getId(), username)).withSelfRel());
        ticketDto.add(linkTo(TicketController.class).slash("/refund").withRel("refund"));
        return new ResponseEntity<>(ticketDto, HttpStatus.OK);
    }

    @Operation(summary = "Refunds a ticket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns operation result..",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OperationResponseDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when ticket isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Returns access.denied when given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns access.denied when ticket owner's username is not the same as given username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns ticket.expired when session is closed or there is less than an hour left for session to close.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/refund")
    public ResponseEntity<OperationResponseDto> refundTicket(@Valid @RequestBody TicketRefundDto ticketRefundDto) {
        checkUserHasPermission(ticketRefundDto.getUsername());

        var ticketId = ticketRefundDto.getTicketId();
        ticketService.refundTicket(ticketRefundDto);
        var response = new OperationResponseDto(true, "Ticket with id: " + ticketId + " refunded...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update ticket by id. Only ADMIN can get all available tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns updated ticket.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when ticket isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400",
                    description = "Returns insufficient.funds when new owner's balance isn't enough to change owner of the ticket..",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<TicketDto> updateTicket(
            @PathVariable("id") Long id, @Valid @RequestBody TicketRequestDto ticketRequestDto) {
        TicketDto ticket = ticketService.updateTicket(id, ticketRequestDto);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

}
