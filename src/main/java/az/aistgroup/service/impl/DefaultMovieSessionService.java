package az.aistgroup.service.impl;

import az.aistgroup.repository.MovieSessionRepository;
import az.aistgroup.service.MovieSessionService;

public class DefaultMovieSessionService implements MovieSessionService {
    private final MovieSessionRepository movieSessionRepository;

    public DefaultMovieSessionService(MovieSessionRepository movieSessionRepository) {
        this.movieSessionRepository = movieSessionRepository;
    }
}
