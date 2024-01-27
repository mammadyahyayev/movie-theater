package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.Hall;
import az.aistgroup.domain.entity.Movie;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.domain.enumeration.MovieGenre;
import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.exception.CapacityExceedException;
import az.aistgroup.exception.InvalidRequestException;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.HallRepository;
import az.aistgroup.repository.MovieRepository;
import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.service.MovieSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;

import static az.aistgroup.domain.enumeration.MovieSessionTime.EVENING;
import static az.aistgroup.domain.enumeration.MovieSessionTime.MORNING;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DefaultMovieSessionServiceTest {

    @Autowired
    MovieSessionService sessionService;

    @Autowired
    HallRepository hallRepository;

    @Autowired
    MovieSessionRepository sessionRepository;

    @Autowired
    MovieRepository movieRepository;

    @MockBean
    Clock clock;

    @Test
    void throwException_whenRequestedMovieDoesNotExist() {
        var movieId = 5L;
        var movieSessionDto = getDefaultSessionDto(movieId, 1L, LocalDateTime.now(), EVENING);

        var exception = assertThrows(ResourceNotFoundException.class, () -> sessionService.addSession(movieSessionDto));
        assertThat(exception.getMessage()).contains(String.valueOf(movieId));
    }

    @Test
    void throwException_whenHallIsNotEmptyToAssignSession() {
        var savedHall = saveDefaultHall();
        var savedMovie = saveDefaultMovie();

        var session = new MovieSession();
        session.setHall(savedHall);
        session.setMovie(savedMovie);

        var dateTime = LocalDateTime.of(2024, JANUARY, 20, 0, 0);
        session.setDate(dateTime);
        session.setSessionTime(MovieSessionTime.MORNING);

        sessionRepository.save(session);

        var movieSessionDto = getDefaultSessionDto(savedMovie.getId(), savedHall.getId(), dateTime, MORNING);

        var exception = assertThrows(ResourceAlreadyExistException.class, () -> sessionService.addSession(movieSessionDto));
        assertThat(exception.getMessage()).contains(String.valueOf(savedHall.getId()));
    }

    @Test
    void throwException_whenLeftTicketsIsMoreThanHallCapacity() {
        var savedHall = saveDefaultHall();
        var savedMovie = saveDefaultMovie();

        var date = LocalDateTime.of(2024, JANUARY, 20, 0, 0);
        var sessionDto = getDefaultSessionDto(savedMovie.getId(), savedHall.getId(), date, MORNING);
        sessionDto.setTicketsLeft(15);

        var exception = assertThrows(CapacityExceedException.class, () -> sessionService.addSession(sessionDto));
        assertThat(exception.getMessage()).contains(String.valueOf(savedHall.getName()));
    }

    @Test
    void throwException_whenLessThanAnHourLeftForSessionTime_onNewSessionCreation() {
        var savedHall = saveDefaultHall();
        var savedMovie = saveDefaultMovie();

        var fixedCurrentTime = setFixedTime(clock, 2024, JANUARY, 1, 9, 39);

        var sessionDto = getDefaultSessionDto(savedMovie.getId(), savedHall.getId(), fixedCurrentTime, MORNING);

        var exception = assertThrows(InvalidRequestException.class, () -> sessionService.addSession(sessionDto));
        assertThat(exception.getMessage()).contains("less than an hour left");
    }

    @Test
    void addMovieSessionSuccessfully() {
        var savedHall = saveDefaultHall();
        var savedMovie = saveDefaultMovie();

        setFixedTime(clock, 2024, JANUARY, 1, 9, 30);

        var date = LocalDateTime.of(2024, JANUARY, 1, 0, 0);
        var sessionDto = getDefaultSessionDto(savedMovie.getId(), savedHall.getId(), date, EVENING);

        var savedMovieSessionDto = sessionService.addSession(sessionDto);
        assertThat(savedMovieSessionDto.getMovieId()).isEqualTo(savedMovie.getId());
        assertThat(savedMovieSessionDto.getHallId()).isEqualTo(savedHall.getId());

        // the left tickets value isn't provided, therefore, by default left tickets value will be hall's capacity
        assertThat(savedMovieSessionDto.getTicketsLeft()).isEqualTo(savedHall.getCapacity());

        assertThat(savedMovieSessionDto.getDate())
                .isEqualTo(LocalDateTime.of(2024, JANUARY, 1, EVENING.getHourOfDay(), 0));
        assertThat(savedMovieSessionDto.getPrice()).isNotNull();
        assertThat(savedMovieSessionDto.getSessionTime()).isNotNull();
        assertThat(savedMovieSessionDto.getMovieId()).isNotNull();
        assertThat(savedMovieSessionDto.getHallId()).isNotNull();
        assertThat(savedMovieSessionDto.getDate()).isNotNull();
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

    private MovieSessionDto getDefaultSessionDto(Long movieId, Long hallId, LocalDateTime date, MovieSessionTime sessionTime) {
        var sessionDto = new MovieSessionDto();
        sessionDto.setDate(date);
        sessionDto.setSessionTime(sessionTime);
        sessionDto.setMovieId(movieId);
        sessionDto.setHallId(hallId);
        sessionDto.setPrice(new BigDecimal("12.25"));
        sessionDto.setClosed(false);
        return sessionDto;
    }

    private LocalDateTime setFixedTime(Clock clock, int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDateTime fixedCurrentTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        Instant instant = fixedCurrentTime.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        return fixedCurrentTime;
    }

    @BeforeEach
    void clean() {
        movieRepository.deleteAll();
        sessionRepository.deleteAll();
        hallRepository.deleteAll();
    }

}