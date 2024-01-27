package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRefundDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.domain.entity.Seat;
import az.aistgroup.domain.entity.Ticket;
import az.aistgroup.domain.entity.User;
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
                .map(TicketDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id: " + id + " not found!"));
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

        user.decreaseBalance(session.getPrice());
        userRepository.save(user);

        session.decreaseLeftTickets();
        sessionRepository.save(session);

        var ticket = new Ticket();
        ticket.setMovieSession(session);
        ticket.setSeat(seat);
        ticket.setUser(user);

        Ticket newTicket = ticketRepository.save(ticket);
        return new TicketDto(newTicket);
    }

    @Override
    @Transactional
    public void refundTicket(TicketRefundDto ticketRefundDto) {
        Objects.requireNonNull(ticketRefundDto, "ticketRefundDto cannot be null!");

        var ticketId = ticketRefundDto.getTicketId();

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));

        if (!ticket.getUser().getUsername().equals(ticketRefundDto.getUsername())) {
            throw new InvalidRequestException("You can't refund because the ticket doesn't belong to you.");
        }

        var currentDateTime = LocalDateTime.now().minusHours(1);
        if (ticket.getMovieSession().getDate().isAfter(currentDateTime)) {
            throw new TicketExpiredException(
                    "You can't refund the ticket, because less than an hour left for session to begin!");
        }

        var user = ticket.getUser();
        user.increaseBalance(ticket.getMovieSession().getPrice());
        userRepository.save(user);

        var session = ticket.getMovieSession();
        if (session.getHall().getCapacity() > session.getTicketsLeft()) {
            session.setTicketsLeft(session.getTicketsLeft() + 1);
        }
        sessionRepository.save(session);

        ticketRepository.deleteById(ticketId);
    }

    @Override
    @Transactional
    public TicketDto updateTicket(final Long id, final TicketRequestDto ticketRequestDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(ticketRequestDto, "ticketRequestDto can not be null!");

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));

        Seat seat = ticket.getSeat();
        MovieSession session = ticket.getMovieSession();
        if (!seat.getId().equals(ticketRequestDto.getSeatId()) ||
                !session.getId().equals(ticketRequestDto.getSessionId())) {
            throw new InvalidRequestException("You can't change seat or movie session. You have to refund the ticket!");
        }


        User oldTicketOwner = ticket.getUser();
        User newTicketOwner;
        var newUsername = ticketRequestDto.getUsername();
        if (!oldTicketOwner.getUsername().equals(newUsername)) {
            newTicketOwner = userRepository.findUserByUsername(newUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User '" + newUsername + " not found!"));

            var ticketPrice = session.getPrice();
            if (newTicketOwner.getBalance().compareTo(ticketPrice) < 0) {
                throw new InsufficientFundsException(
                        "Sorry! You don't have enough balance in your account to purchase ticket!");
            }

            newTicketOwner.decreaseBalance(ticketPrice);
            userRepository.save(newTicketOwner);

            oldTicketOwner.increaseBalance(ticketPrice);
            userRepository.save(oldTicketOwner);

            ticket.setUser(newTicketOwner);
        }
        Ticket updatedTicket = ticketRepository.save(ticket);
        return new TicketDto(updatedTicket);
    }
}
