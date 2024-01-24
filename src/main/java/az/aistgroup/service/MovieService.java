package az.aistgroup.service;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.dto.MovieViewDto;

import java.util.List;

public interface MovieService {
    List<MovieViewDto> getAllMovies();

    MovieDto getMovieById(Long id);

    MovieDto addMovie(MovieDto movieDto);

    MovieDto updateMovie(Long id, MovieDto movieDto);

    void deleteMovie(Long id);

    List<MovieDto> searchMoviesByName(String movieName);
}
