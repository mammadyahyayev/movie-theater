package az.aistgroup.service.impl;

import az.aistgroup.repository.MovieRepository;
import az.aistgroup.service.MovieService;

public class DefaultMovieService implements MovieService {
private final MovieRepository movieRepository;

    public DefaultMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
}
