package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.dto.MovieSearchCriteria;
import az.aistgroup.domain.entity.Movie;
import az.aistgroup.domain.mapper.MovieMapper;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.MovieRepository;
import az.aistgroup.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultMovieService implements MovieService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMovieService.class);

    private final MovieRepository movieRepository;

    public DefaultMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieDto::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
    }

    @Override
    @Transactional
    public MovieDto addMovie(MovieDto movieDto) {
        Objects.requireNonNull(movieDto, "movieDto can not be null!");

        var movie = MovieMapper.toEntity(movieDto);
        Movie newMovie = movieRepository.save(movie);

        LOG.debug(newMovie.getName() + " is added.");
        return MovieMapper.toDto(newMovie);
    }

    @Override
    @Transactional
    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(movieDto, "movieDto can not be null!");

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));

        MovieMapper.toEntityInPlace(movieDto, movie);
        Movie updatedMovie = movieRepository.save(movie);
        LOG.debug("Movie with id: " + id + " is updated!");
        return MovieMapper.toDto(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        var movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));

        movieRepository.delete(movie);
        LOG.debug("Movie with id: " + id + " is deleted!");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> searchMovies(MovieSearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria cannot be null!");
        List<Movie> movies = movieRepository.findByCriteria(criteria);
        return movies.stream()
                .map(MovieMapper::toDto)
                .toList();
    }
}
