package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.dto.TicketRefundDto;
import az.aistgroup.domain.dto.TicketRequestDto;
import az.aistgroup.domain.entity.*;
import az.aistgroup.domain.enumeration.MovieGenre;
import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.exception.*;
import az.aistgroup.repository.*;
import az.aistgroup.service.MovieSessionService;
import az.aistgroup.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DefaultTicketServiceTest {

    private static final String DEFAULT_USERNAME = "jack";
    public static final String DEFAULT_USER_BALANCE = "120";

    @Autowired
    MovieSessionService sessionService;

    @Autowired
    HallRepository hallRepository;

    @Autowired
    MovieSessionRepository sessionRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketService ticketService;

    @Autowired
    SeatRepository seatRepository;

    @MockBean
    Clock clock;

    @Test
    void throwException_whenTicketDoesNotBelongToUser() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser(DEFAULT_USER_BALANCE);
        var savedMovieSession = sessionRepository.save(getDefaultMovieSession(saveDefaultMovie(), savedHall));

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);
        var savedTicket = ticketRepository.save(ticket);

        var ex = assertThrows(InvalidRequestException.class, () -> ticketService.getTicketById(savedTicket.getId(), "rose"));
        assertThat(ex.getMessage()).isEqualTo("The ticket doesn't belong to you!");
    }

    @Test
    void canNotBuyTicket_afterSessionClosed() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser(DEFAULT_USER_BALANCE);

        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setClosed(true);
        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);
        ticketRepository.save(ticket);

        var ticketRequestDto = getDefaultTicketRequestDto(savedMovieSession, savedSeat);

        var ex = assertThrows(InvalidRequestException.class, () -> ticketService.buyTicket(ticketRequestDto));
        assertThat(ex.getMessage()).isEqualTo("You can't buy a ticket because Session is already finished!");
    }

    @Test
    void canNotBuyTicket_whenThereAreNoTicketsLeft() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser(DEFAULT_USER_BALANCE);

        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setTicketsLeft(0);
        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);

        ticketRepository.save(ticket);

        var ticketRequestDto = getDefaultTicketRequestDto(savedMovieSession, savedSeat);

        assertThrows(NoTicketsAvailableException.class, () -> ticketService.buyTicket(ticketRequestDto));
    }

    private static TicketRequestDto getDefaultTicketRequestDto(MovieSession movieSession, Seat seat) {
        return getDefaultTicketRequestDto(DEFAULT_USERNAME, movieSession, seat);
    }

    private static TicketRequestDto getDefaultTicketRequestDto(String username, MovieSession movieSession, Seat seat) {
        var ticketRequestDto = new TicketRequestDto();
        ticketRequestDto.setUsername(username);
        ticketRequestDto.setSessionId(movieSession.getId());
        ticketRequestDto.setSeatId(seat.getId());
        return ticketRequestDto;
    }

    @Test
    void canNotBuyTicket_becauseSeatIsAlreadyBooked() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser(DEFAULT_USER_BALANCE);
        var savedMovieSession = sessionRepository.save(getDefaultMovieSession(saveDefaultMovie(), savedHall));

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);

        ticketRepository.save(ticket);

        var ticketRequestDto = getDefaultTicketRequestDto(savedMovieSession, savedSeat);

        var ex = assertThrows(SeatAlreadyBookedException.class, () -> ticketService.buyTicket(ticketRequestDto));
        assertThat(ex.getMessage()).containsIgnoringCase(String.valueOf(savedSeat.getSeatNum()));
    }

    @Test
    void canNotBuyTicket_becauseBalanceIsNotEnough() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser("3");

        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setPrice(new BigDecimal("12"));
        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticketRequestDto = getDefaultTicketRequestDto(savedUser.getUsername(), savedMovieSession, savedSeat);

        var ex = assertThrows(InsufficientFundsException.class, () -> ticketService.buyTicket(ticketRequestDto));
        assertThat(ex.getMessage())
                .containsIgnoringCase("Sorry! You don't have enough balance in your account to purchase ticket!");
    }

    @Test
    void canBuyTicketSuccessFully() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser("13");

        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setPrice(new BigDecimal("12"));
        defaultMovieSession.setTicketsLeft(1);
        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticketRequestDto = getDefaultTicketRequestDto(savedUser.getUsername(), savedMovieSession, savedSeat);

        BigDecimal balanceBefore = savedUser.getBalance();
        BigDecimal expectedBalance = balanceBefore.subtract(savedMovieSession.getPrice());

        TicketDto ticketDto = ticketService.buyTicket(ticketRequestDto);
        assertThat(ticketDto.getTicketHolder()).isEqualTo(savedUser.getFullName());

        Optional<User> user = userRepository.findUserByUsername(savedUser.getUsername());
        assertThat(user.isPresent()).isTrue();
        assertThat(user.get().getBalance().doubleValue()).isEqualTo(expectedBalance.doubleValue());

        Optional<MovieSession> movieSession = sessionRepository.findById(savedMovieSession.getId());
        assertThat(movieSession.isPresent()).isTrue();
        assertThat(movieSession.get().isClosed()).isTrue();
        assertThat(movieSession.get().getTicketsLeft()).isZero();
    }


    @Test
    void canNotRefund_becauseLessThanAnHourLeftForSessionToBegin() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser("13");

        var sessionDateTime = LocalDateTime.of(2024, Month.JANUARY, 10, 0, 0);
        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setDate(sessionDateTime);
        defaultMovieSession.setSessionTime(MovieSessionTime.MORNING);
        defaultMovieSession.setTicketsLeft(1);
        defaultMovieSession.setPrice(new BigDecimal("12"));

        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);

        var savedTicket = ticketRepository.save(ticket);

        var ticketRefundDto = new TicketRefundDto();
        ticketRefundDto.setUsername(savedUser.getUsername());
        ticketRefundDto.setTicketId(savedTicket.getId());

        setFixedTime(clock, 2024, Month.JANUARY, 10, 9, 30);

        var ex = assertThrows(TicketExpiredException.class, () -> ticketService.refundTicket(ticketRefundDto));
        assertThat(ex.getMessage())
                .isEqualTo("You can't refund the ticket, because less than an hour left for session to begin!");
    }

    @Test
    void canSuccessFullyRefund() {
        var savedHall = saveDefaultHall();
        var savedSeat = saveDefaultSeat(savedHall, 1);
        var savedUser = saveDefaultUser("13");

        var sessionDateTime = LocalDateTime.of(2024, Month.JANUARY, 10, 0, 0);
        var defaultMovieSession = getDefaultMovieSession(saveDefaultMovie(), savedHall);
        defaultMovieSession.setDate(sessionDateTime);
        defaultMovieSession.setSessionTime(MovieSessionTime.MORNING);
        defaultMovieSession.setTicketsLeft(1);
        defaultMovieSession.setPrice(new BigDecimal("12"));

        var savedMovieSession = sessionRepository.save(defaultMovieSession);

        var ticket = getDefaultTicket(savedUser, savedMovieSession, savedSeat);

        var savedTicket = ticketRepository.save(ticket);

        var ticketRefundDto = new TicketRefundDto();
        ticketRefundDto.setUsername(savedUser.getUsername());
        ticketRefundDto.setTicketId(savedTicket.getId());

        setFixedTime(clock, 2024, Month.JANUARY, 10, 8, 30);

        BigDecimal balanceBeforeRefund = savedUser.getBalance();
        BigDecimal expectedBalanceAfterRefund = balanceBeforeRefund.add(savedMovieSession.getPrice());

        int leftTicketsBeforeRefund = savedMovieSession.getTicketsLeft();

        ticketService.refundTicket(ticketRefundDto);

        assertThat(savedUser.getBalance().doubleValue()).isEqualTo(expectedBalanceAfterRefund.doubleValue());
        assertThat(savedMovieSession.isClosed()).isFalse();
        assertThat(savedMovieSession.getTicketsLeft()).isEqualTo(leftTicketsBeforeRefund + 1);

        Optional<Ticket> refundedTicket = ticketRepository.findById(savedTicket.getId());
        assertThat(refundedTicket.isEmpty()).isTrue();
    }

    private User saveDefaultUser(String balance) {
        var user = new User();
        user.setFirstName("Jack");
        user.setLastName("Jackson");
        user.setFatherName("Doe");
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword("12345678");
        user.setBalance(new BigDecimal(balance));
        return userRepository.save(user);
    }

    private MovieSession getDefaultMovieSession(Movie movie, Hall hall) {
        var movieSession = new MovieSession();
        movieSession.setMovie(movie);
        movieSession.setHall(hall);
        movieSession.setDate(LocalDateTime.now());
        movieSession.setSessionTime(MovieSessionTime.MORNING);
        movieSession.setPrice(new BigDecimal("12"));
        movieSession.setTicketsLeft(10);
        movieSession.setClosed(false);
        return movieSession;
    }

    private Seat saveDefaultSeat(Hall hall, int seatNum) {
        var seat = new Seat();
        seat.setHall(hall);
        seat.setSeatNum(seatNum);
        return seatRepository.save(seat);
    }

    private Hall saveDefaultHall() {
        var hall = new Hall();
        hall.setName("Hall 1");
        hall.setCapacity(10);
        return hallRepository.save(hall);
    }

    private Movie saveDefaultMovie() {
        var movie = new Movie();
        movie.setName("The Dark Knight");
        movie.setGenre(MovieGenre.ADVENTURE);
        return movieRepository.save(movie);
    }

    private void setFixedTime(Clock clock, int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDateTime fixedCurrentTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        Instant instant = fixedCurrentTime.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    private static Ticket getDefaultTicket(User savedUser, MovieSession savedMovieSession, Seat savedSeat) {
        var ticket = new Ticket();
        ticket.setUser(savedUser);
        ticket.setMovieSession(savedMovieSession);
        ticket.setSeat(savedSeat);
        return ticket;
    }

    @BeforeEach
    void clean() {
        movieRepository.deleteAll();
        sessionRepository.deleteAll();
        hallRepository.deleteAll();
        userRepository.deleteAll();
        ticketRepository.deleteAll();
    }

}