package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.service.MovieSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultMovieSessionService implements MovieSessionService {
    private final MovieSessionRepository movieSessionRepository;

    public DefaultMovieSessionService(MovieSessionRepository movieSessionRepository) {
        this.movieSessionRepository = movieSessionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieSessionDto> getSessions() {
        return movieSessionRepository.getAllMovieSessions();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieSessionDto getSessionById(Long id) {
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
    public MovieSessionDto addSession(MovieSessionDto sessionDto) {
        //TODO: Set movie in here
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");

        var movieSession = new MovieSession();
        movieSession.setDate(sessionDto.getDate());
        movieSession.setSessionTime(sessionDto.getSessionTime());
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());

        MovieSession newSession = movieSessionRepository.save(movieSession);
        var movieSessionDto = new MovieSessionDto();
        movieSessionDto.setPrice(newSession.getPrice());
        movieSessionDto.setSessionTime(newSession.getSessionTime());
        movieSessionDto.setDate(newSession.getDate());
        movieSessionDto.setTicketsLeft(newSession.getTicketsLeft());
        // movieSessionDto.setPrice(newSession.getMovie());
        return movieSessionDto;
    }

    @Override
    @Transactional
    public MovieSessionDto updateSession(final Long id, final MovieSessionDto sessionDto) {
        //TODO: Set movie in here
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(sessionDto, "sessionDto can not be null!");

        MovieSession movieSession = movieSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session with " + id + " not found!"));

        movieSession.setDate(sessionDto.getDate());
        movieSession.setSessionTime(sessionDto.getSessionTime());
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());

        MovieSession updatedSession = movieSessionRepository.save(movieSession);
        var movieSessionDto = new MovieSessionDto();
        movieSessionDto.setPrice(updatedSession.getPrice());
        movieSessionDto.setSessionTime(updatedSession.getSessionTime());
        movieSessionDto.setDate(updatedSession.getDate());
        movieSessionDto.setTicketsLeft(updatedSession.getTicketsLeft());

        // movieSessionDto.setPrice(updatedSession.getMovie());
        return movieSessionDto;
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
