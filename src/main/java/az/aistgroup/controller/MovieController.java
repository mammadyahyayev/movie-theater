package az.aistgroup.controller;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable("id") Long id) {
        MovieDto movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MovieDto> addMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto movie = movieService.addMovie(movieDto);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable("id") Long id, @Valid @RequestBody MovieDto movieDto) {
        MovieDto movie = movieService.updateMovie(id, movieDto);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponseDto> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovie(id);
        var response = new OperationResponseDto(true, "Movie with " + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
