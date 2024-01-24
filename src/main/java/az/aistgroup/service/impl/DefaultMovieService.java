package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.entity.Movie;
import az.aistgroup.domain.mapper.MovieMapper;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.MovieRepository;
import az.aistgroup.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultMovieService implements MovieService {
    private final MovieRepository movieRepository;

    public DefaultMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.getAllMovies();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto getMovieById(final Long id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with " + id + " not found!"));
    }

    @Override
    @Transactional
    public MovieDto addMovie(final MovieDto movieDto) {
        Objects.requireNonNull(movieDto, "movieDto can not be null!");

        var movie = MovieMapper.toEntity(movieDto);
        Movie newMovie = movieRepository.save(movie);

        return MovieMapper.toDto(newMovie);
    }

    @Override
    @Transactional
    public MovieDto updateMovie(final Long id, final MovieDto movieDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(movieDto, "movieDto can not be null!");

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with " + id + " not found!"));

        movie.setName(movieDto.getName());
        movie.setGenre(movieDto.getGenre());

        Movie updatedMovie = movieRepository.save(movie);
        return MovieMapper.toDto(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        movieRepository.findById(id)
                .ifPresentOrElse(movieRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Movie with " + id + " not found!");
                        });
    }

    @Override
    public List<MovieDto> searchMoviesByName(String movieName) {
        return movieRepository.findByNameIsContainingIgnoreCase(movieName);
    }
}
