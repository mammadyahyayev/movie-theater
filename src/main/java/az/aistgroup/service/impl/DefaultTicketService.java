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

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultTicketService implements TicketService {
    private final TicketRepository ticketRepository;
    private final MovieSessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public DefaultTicketService(
            TicketRepository ticketRepository,
            MovieSessionRepository sessionRepository,
            SeatRepository seatRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.ticketRepository = ticketRepository;
        this.sessionRepository = sessionRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketDto::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDto getTicketById(Long id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));

        if (!ticket.getUser().getUsername().equals(username)) {
            throw new InvalidRequestException("The ticket doesn't belong to you!");
        }

        return new TicketDto(ticket);
    }

    @Override
    @Transactional
    public TicketDto buyTicket(final TicketRequestDto ticketRequestDto) {
        Objects.requireNonNull(ticketRequestDto, "ticketRequestDto can not be null!");

        // check there are enough tickets to buy
        var session = sessionRepository.findById(ticketRequestDto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session", ticketRequestDto.getSessionId()));

        if (session.isClosed()) {
            throw new InvalidRequestException("You can't buy a ticket because Session is already finished!");
        }

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

        var user = userRepository.findUserByUsername(ticketRequestDto.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User '" + ticketRequestDto.getUsername() + " not found!"));

        // check user has enough money to buy
        if (user.getBalance().compareTo(session.getPrice()) < 0) {
            throw new InsufficientFundsException(
                    "Sorry! You don't have enough balance in your account to purchase ticket!");
        }

        user.decreaseBalance(session.getPrice());
        userRepository.save(user);

        session.decreaseLeftTickets();
        if (session.getTicketsLeft() == 0) {
            session.setClosed(true);
        }
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

        MovieSession movieSession = ticket.getMovieSession();

        if (movieSession.isClosed()) {
            throw new TicketExpiredException("You can't refund the ticket, because session is finished!");
        }

        int hourOfDay = movieSession.getSessionHourOfDay();
        LocalDateTime sessionTime = movieSession.getDate().withHour(hourOfDay);
        LocalDateTime now = LocalDateTime.now(clock);
        Duration diff = Duration.between(now, sessionTime);
        if (diff.toMinutes() < 60 && diff.toMinutes() > 0) {
            throw new TicketExpiredException(
                    "You can't refund the ticket, because less than an hour left for session to begin!");
        }

        var user = ticket.getUser();
        user.increaseBalance(movieSession.getPrice());
        userRepository.save(user);

        if (movieSession.getHallCapacity() > movieSession.getTicketsLeft()) {
            movieSession.setTicketsLeft(movieSession.getTicketsLeft() + 1);
            movieSession.setClosed(false);
        }
        sessionRepository.save(movieSession);

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
