package az.aistgroup.controller;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.service.MovieSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie-sessions")
public class MovieSessionController {
    private final MovieSessionService movieSessionService;

    public MovieSessionController(MovieSessionService movieSessionService) {
        this.movieSessionService = movieSessionService;
    }

    @GetMapping
    public ResponseEntity<List<MovieSessionDto>> getAllSessions() {
        List<MovieSessionDto> movies = movieSessionService.getSessions();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieSessionDto> getMovieSessionById(@PathVariable("id") Long id) {
        MovieSessionDto movie = movieSessionService.getSessionById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MovieSessionDto> addMovieSession(@Valid @RequestBody MovieSessionDto sessionDto) {
        MovieSessionDto movie = movieSessionService.addSession(sessionDto);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieSessionDto> updateMovieSession(
            @PathVariable("id") Long id,
            @Valid @RequestBody MovieSessionDto sessionDto
    ) {
        MovieSessionDto movie = movieSessionService.updateSession(id, sessionDto);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponseDto> deleteMovieSession(@PathVariable("id") Long id) {
        movieSessionService.deleteSession(id);
        var response = new OperationResponseDto(true, "Session with " + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
