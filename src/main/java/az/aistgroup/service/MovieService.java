package az.aistgroup.service;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.dto.MovieSearchCriteria;

import java.util.List;

public interface MovieService {
    List<MovieDto> getAllMovies();

    MovieDto getMovieById(Long id);

    MovieDto addMovie(MovieDto movieDto);

    MovieDto updateMovie(Long id, MovieDto movieDto);

    void deleteMovie(Long id);

    List<MovieDto> searchMovies(MovieSearchCriteria movieSearchCriteria);
}
