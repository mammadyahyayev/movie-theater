package az.aistgroup.controller;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.exception.ErrorResponse;
import az.aistgroup.exception.InvalidRequestException;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.MovieService;
import az.aistgroup.util.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Fetches all movies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all available movies.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))})
    })
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @Operation(summary = "Fetch a movie by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a movie for given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns when movie isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable("id") Long id) {
        MovieDto movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @Operation(summary = "Adds a movie. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a newly added movie.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation errors happened.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<MovieDto> addMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto movie = movieService.addMovie(movieDto);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a movie. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a newly updated movie.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation errors happened.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable("id") Long id, @Valid @RequestBody MovieDto movieDto) {
        MovieDto movie = movieService.updateMovie(id, movieDto);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @Operation(summary = "Deletes a movie. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a delete operation result.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OperationResponseDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<OperationResponseDto> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovie(id);
        var response = new OperationResponseDto(true, "Movie with id:" + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Returns list of movies based on given movie name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a searched movies.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),

            @ApiResponse(responseCode = "400", description = "Returns validation.error when given movie name is blank.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(@RequestParam("name") String name) {
        if (!Strings.hasText(name)) {
            throw new InvalidRequestException("Movie name can not be empty!");
        }

        List<MovieDto> movies = movieService.searchMoviesByName(name);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
