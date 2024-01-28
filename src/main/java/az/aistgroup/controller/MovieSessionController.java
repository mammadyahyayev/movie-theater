package az.aistgroup.controller;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.dto.MovieSessionView;
import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.exception.ErrorResponse;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.MovieSessionService;
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
@RequestMapping("/api/v1/movie-sessions")
public class MovieSessionController {
    private final MovieSessionService movieSessionService;

    public MovieSessionController(MovieSessionService movieSessionService) {
        this.movieSessionService = movieSessionService;
    }

    @Operation(summary = "Fetches all movie sessions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all available movie sessions.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieSessionView.class))})
    })
    @GetMapping
    public ResponseEntity<List<MovieSessionView>> getAllSessions() {
        List<MovieSessionView> movies = movieSessionService.getSessions();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @Operation(summary = "Fetch a movie session by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a movie session for given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieSessionDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns when movie session isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieSessionDto> getMovieSessionById(@PathVariable("id") Long id) {
        MovieSessionDto movie = movieSessionService.getSessionById(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @Operation(summary = "Adds a movie session. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a newly added movie session.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieSessionDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie or hall isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns resource.already.exist when hall is not empty for requested date.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns capacity.exceed when ticketsLeft is more than hall's capacity.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation errors happened.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<MovieSessionDto> addMovieSession(@Valid @RequestBody MovieSessionDto sessionDto) {
        MovieSessionDto movie = movieSessionService.addSession(sessionDto);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @Operation(summary = "Updated a movie session by given id. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a newly updated movie session.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieSessionDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie session isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie or hall isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns resource.already.exist when hall is not empty for requested date.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns capacity.exceed when ticketsLeft is more than hall's capacity.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation errors happened.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<MovieSessionDto> updateMovieSession(
            @PathVariable("id") Long id,
            @Valid @RequestBody MovieSessionDto sessionDto
    ) {
        MovieSessionDto movie = movieSessionService.updateSession(id, sessionDto);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @Operation(summary = "Deletes a movie session by given id. Only ADMIN can do this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return delete movie session operation result.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OperationResponseDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns resource.not.found when movie session isn't found with given id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<OperationResponseDto> deleteMovieSession(@PathVariable("id") Long id) {
        movieSessionService.deleteSession(id);
        var response = new OperationResponseDto(true, "Session with id: " + id + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
