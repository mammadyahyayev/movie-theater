package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.dto.MovieSessionView;
import az.aistgroup.domain.entity.Hall;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.domain.mapper.MovieSessionMapper;
import az.aistgroup.exception.HallCapacityExceedException;
import az.aistgroup.exception.InvalidRequestException;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.HallRepository;
import az.aistgroup.repository.MovieRepository;
import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.service.MovieSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultMovieSessionService implements MovieSessionService {
    private final MovieSessionRepository movieSessionRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final Clock clock;

    public DefaultMovieSessionService(
            MovieSessionRepository movieSessionRepository,
            MovieRepository movieRepository,
            HallRepository hallRepository,
            Clock clock
    ) {
        this.movieSessionRepository = movieSessionRepository;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieSessionView> getSessions() {
        return movieSessionRepository.getAllMovieSessions();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieSessionDto getSessionById(final Long id) {
        return movieSessionRepository.findById(id)
                .map(MovieSessionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));
    }

    @Override
    @Transactional
    public MovieSessionDto addSession(MovieSessionDto sessionDto) {
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");
        Objects.requireNonNull(sessionDto.getPrice(), "Price of movie session can not be null!");
        Objects.requireNonNull(sessionDto.getMovieId(), "movieId can not be null!");
        Objects.requireNonNull(sessionDto.getHallId(), "hallId can not be null!");
        Objects.requireNonNull(sessionDto.getDate(), "date of the movie session can not be null!");
        Objects.requireNonNull(sessionDto.getSessionTime(), "sessionTime can not be null!");

        var movieSession = MovieSessionMapper.toEntity(sessionDto);

        var movie = movieRepository.findById(sessionDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", sessionDto.getMovieId()));
        movieSession.setMovie(movie);

        // checks whether there is active movie session for the hall at the given time
        sessionDto.setDate(sessionDto.getDate().withHour(0).withMinute(0));
        var movieSessionTime = MovieSessionTime.valueOf(sessionDto.getSessionTime().toUpperCase());
        var hall = getEmptyHallBySession(sessionDto.getHallId(), sessionDto.getDate(), movieSessionTime);
        movieSession.setHall(hall);

        if (sessionDto.getTicketsLeft() == 0) {
            movieSession.setTicketsLeft(hall.getCapacity());
        } else if (sessionDto.getTicketsLeft() > hall.getCapacity()) {
            String message = MessageFormat
                    .format("Number of tickets can not be more than capacity ({0}) of {1}",
                            hall.getCapacity(), hall.getName());
            throw new HallCapacityExceedException(message);
        }

        Duration diff = differenceBetweenNowAndSessionTime(sessionDto);
        if (diff.toMinutes() < 60 && diff.toMinutes() > 0) {
            throw new InvalidRequestException(
                    "You can't create a new session because less than an hour left for " + sessionDto.getSessionTime() + " session!");
        }

        int hourOfDay = movieSessionTime.getHourOfDay();
        LocalDateTime sessionTime = sessionDto.getDate().withHour(hourOfDay);
        movieSession.setDate(sessionTime);
        movieSession.setClosed(sessionDto.isClosed());

        MovieSession newSession = movieSessionRepository.save(movieSession);
        return MovieSessionMapper.toDto(newSession);
    }

    @Override
    @Transactional
    public MovieSessionDto updateSession(final Long id, final MovieSessionDto sessionDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");

        MovieSession movieSession = movieSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));

        Duration diff = differenceBetweenNowAndSessionTime(sessionDto);
        if (diff.toMinutes() < 60 && diff.toMinutes() > 0) {
            throw new InvalidRequestException(
                    "You can't update session because less than an hour left for " + sessionDto.getSessionTime() + " session!");
        }

        MovieSessionMapper.toEntityInPlace(sessionDto, movieSession);

        // update Movie
        if (movieSession.getMovie() != null && !movieSession.getMovie().getId().equals(sessionDto.getMovieId())) {
            var movie = movieRepository.findById(sessionDto.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie", sessionDto.getMovieId()));
            movieSession.setMovie(movie);
        }

        // update Hall
        Hall hall = movieSession.getHall();
        if (movieSession.getHall() != null && !hall.getId().equals(sessionDto.getHallId())) {
            var movieSessionTime = MovieSessionTime.valueOf(sessionDto.getSessionTime().toUpperCase());
            hall = getEmptyHallBySession(sessionDto.getHallId(), sessionDto.getDate(), movieSessionTime);
            movieSession.setHall(hall);
        }

        if (sessionDto.getTicketsLeft() > hall.getCapacity()) {
            String message = MessageFormat
                    .format("Number of tickets can not be more than capacity({0}) of {1}",
                            hall.getCapacity(), hall.getName());
            throw new HallCapacityExceedException(message);
        } else {
            movieSession.setTicketsLeft(sessionDto.getTicketsLeft());
        }

        movieSession.setClosed(sessionDto.isClosed());

        var updatedSession = movieSessionRepository.save(movieSession);
        return MovieSessionMapper.toDto(updatedSession);
    }


    @Override
    @Transactional
    public void deleteSession(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        var movieSession = movieSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));

        movieSessionRepository.delete(movieSession);
    }

    /**
     * Schedules a job that will be executed in every 12 hours and will
     * close past movie sessions.
     */
    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void deleteInActiveUsers() {
        this.movieSessionRepository.findAllPastMovieSessions(LocalDateTime.now().minusDays(1))
                .forEach(movieSession -> {
                    movieSession.setClosed(true);
                    this.movieSessionRepository.save(movieSession);
                });
    }

    /**
     * The method will find the empty hall for the given date. If there is a hall and
     * it has active movie session, then {@link ResourceAlreadyExistException} will
     * be thrown.
     *
     * @param hallId      id of the {@link Hall}
     * @param sessionDate date of the movie session
     * @return the {@link Hall} which doesn't have assigned {@link MovieSession}.
     * @throws ResourceAlreadyExistException will be thrown when there is assigned movie session
     *                                       for {@link Hall}
     * @throws ResourceNotFoundException     will be thrown when there is no {@link Hall}
     *                                       for given {@code hallId}.
     */
    private Hall getEmptyHallBySession(final Long hallId, final LocalDateTime sessionDate, final MovieSessionTime session) {
        movieSessionRepository.findActiveSessionForHall(hallId, sessionDate, session)
                .ifPresent((ms) -> {
                    throw new ResourceAlreadyExistException("There is a session for hall id: " + hallId);
                });

        return hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Hall", hallId));
    }

    /**
     * Finds difference between current time and {@link MovieSession} time.
     *
     * @param sessionDto a Dto class
     * @return {@link Duration} between dates.
     */
    private Duration differenceBetweenNowAndSessionTime(MovieSessionDto sessionDto) {
        MovieSessionTime movieSessionTime = MovieSessionTime.valueOf(sessionDto.getSessionTime().toUpperCase());
        int hourOfDay = movieSessionTime.getHourOfDay();
        LocalDateTime sessionDateTime = sessionDto.getDate().withHour(hourOfDay);
        LocalDateTime now = LocalDateTime.now(clock);
        return Duration.between(now, sessionDateTime);
    }

}
