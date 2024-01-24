package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.domain.mapper.MovieSessionMapper;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.MovieRepository;
import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.service.MovieSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultMovieSessionService implements MovieSessionService {
    private final MovieSessionRepository movieSessionRepository;
    private final MovieRepository movieRepository;

    public DefaultMovieSessionService(
            MovieSessionRepository movieSessionRepository,
            MovieRepository movieRepository
    ) {
        this.movieSessionRepository = movieSessionRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieSessionDto> getSessions() {
        return movieSessionRepository.getAllMovieSessions();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieSessionDto getSessionById(final Long id) {
        return movieSessionRepository.findById(id)
                .map(session -> {
                    var sessionDto = new MovieSessionDto();
                    sessionDto.setDate(session.getDate());
                    sessionDto.setSessionTime(session.getSessionTime());
                    sessionDto.setTicketsLeft(session.getTicketsLeft());
                    sessionDto.setPrice(session.getPrice());
                    return sessionDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Session with " + id + " not found!"));
    }

    @Override
    @Transactional
    public MovieSessionDto addSession(final MovieSessionDto sessionDto) {
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");

        var movieSession = MovieSessionMapper.toEntity(sessionDto);
        movieRepository.findById(sessionDto.getMovieId())
                .ifPresentOrElse(movieSession::setMovie,
                        () -> {
                            throw new ResourceNotFoundException("Movie with " + sessionDto.getMovieId() + " not found!");
                        });

        movieSessionRepository.findByHallId(sessionDto.getHallId())
                .ifPresent((session) -> {
                    throw new ResourceAlreadyExistException("There is a session for hall id: " + sessionDto.getHallId());
                });

        MovieSession newSession = movieSessionRepository.save(movieSession);
        return MovieSessionMapper.toDto(newSession);
    }

    @Override
    @Transactional
    public MovieSessionDto updateSession(final Long id, final MovieSessionDto sessionDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");

        MovieSession movieSession = movieSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session with " + id + " not found!"));

        MovieSessionMapper.toEntityInPlace(sessionDto, movieSession);

        if (!movieSession.getMovie().getId().equals(sessionDto.getMovieId())) {
            movieRepository.findById(sessionDto.getMovieId())
                    .ifPresentOrElse(movieSession::setMovie,
                            () -> {
                                throw new ResourceNotFoundException("Movie with " + sessionDto.getMovieId() + " not found!");
                            });
        }

        var updatedSession = movieSessionRepository.save(movieSession);
        return MovieSessionMapper.toDto(updatedSession);
    }

    @Override
    @Transactional
    public void deleteSession(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        movieSessionRepository.findById(id)
                .ifPresentOrElse(movieSessionRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Session with " + id + " not found!");
                        });
    }

}
