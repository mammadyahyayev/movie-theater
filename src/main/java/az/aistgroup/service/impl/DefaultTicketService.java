package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.domain.entity.Ticket;
import az.aistgroup.exception.*;
import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.repository.SeatRepository;
import az.aistgroup.repository.TicketRepository;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.service.TicketService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultTicketService implements TicketService {
    private final TicketRepository ticketRepository;
    private final MovieSessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public DefaultTicketService(
            TicketRepository ticketRepository,
            MovieSessionRepository sessionRepository,
            SeatRepository seatRepository,
            UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.sessionRepository = sessionRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll().stream().map(ticket -> {
            var ticketDto = new TicketDto();

            if (ticket.getMovieSession() != null) {
                sessionRepository.findById(ticket.getMovieSession().getId())
                        .ifPresent((session) -> {
                            String sessionTime = session.getDate() + " " + session.getSessionTime();
                            ticketDto.setSession(sessionTime);
                            ticketDto.setMovie(session.getMovie().getName());
                        });
            }

            if (ticket.getSeat() != null) {
                seatRepository.findById(ticket.getSeat().getId())
                        .ifPresent((seat) -> {
                            ticketDto.setSeat(seat.getHall().getName() + " " + seat.getSeatNum());
                        });
            }

            if (ticket.getUser() != null) {
                userRepository.findById(ticket.getUser().getId())
                        .ifPresent(user -> {
                            ticketDto.setTicketHolder(user.getFullName());
                        });
            }

            return ticketDto;
        }).toList();
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
    public TicketDto buyTicket(final TicketRequestDto ticketRequestDto) {
        Objects.requireNonNull(ticketRequestDto, "ticketRequestDto can not be null!");

        // check there are enough tickets to buy
        var session = sessionRepository.findById(ticketRequestDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session", ticketRequestDto.getSessionId()));

        if (session.getTicketsLeft() == 0) {
            throw new NoTicketsAvailableException();
        }

        // check seat is booked or not
        var existingTicket = ticketRepository
                .findTicketBySeatInCurrentSession(ticketRequestDto.getSeatId(), ticketRequestDto.getSessionId());

        if (existingTicket.isPresent()) {
            throw new SeatAlreadyBookedException(existingTicket.get().getSeat().getSeatNum());
        }

        var seat = seatRepository.findById(ticketRequestDto.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat", ticketRequestDto.getSeatId()));

        // check user has enough money to buy
        var user = userRepository.findUserByUsername(ticketRequestDto.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User '" + ticketRequestDto.getUsername() + " not found!"));

        if (user.getBalance().compareTo(session.getPrice()) < 0) {
            throw new InsufficientFundsException(
                    "Sorry! You don't have enough balance in your account to purchase ticket!");
        }

        session.decreaseLeftTickets();
        sessionRepository.save(session);

        user.setBalance(user.getBalance().subtract(session.getPrice()));
        userRepository.save(user);

        var ticket = new Ticket();
        ticket.setMovieSession(session);
        ticket.setSeat(seat);
        ticket.setUser(user);

        Ticket newTicket = ticketRepository.save(ticket);
        return new TicketDto(newTicket);
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
    public void refundTicket(Long ticketId) {
        Objects.requireNonNull(ticketId, "ticketId cannot be null!");

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));

        var currentDateTime = LocalDateTime.now();
        if (ticket.getMovieSession().getDate().isAfter(currentDateTime)) {
            throw new TicketExpiredException(ticketId);
        }

        var user = ticket.getUser();
        user.setBalance(user.getBalance().add(ticket.getMovieSession().getPrice()));
        userRepository.save(user);

        var session = ticket.getMovieSession();
        if (session.getHall().getCapacity() > session.getTicketsLeft()) {
            session.setTicketsLeft(session.getTicketsLeft() + 1);
        }
        sessionRepository.save(session);

        ticketRepository.deleteById(ticketId);
    }
}
