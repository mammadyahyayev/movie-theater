package az.aistgroup.domain.mapper;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import az.aistgroup.domain.enumeration.MovieSessionTime;

public final class MovieSessionMapper {

    private MovieSessionMapper() {
    }

    public static MovieSession toEntity(MovieSessionDto sessionDto) {
        var movieSession = new MovieSession();
        MovieSessionTime sessionTime = MovieSessionTime.valueOf(sessionDto.getSessionTime().toUpperCase());
        movieSession.setDate(sessionDto.getDate().withHour(sessionTime.getHourOfDay()));
        movieSession.setSessionTime(sessionTime);
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());
        return movieSession;
    }

    public static void toEntityInPlace(MovieSessionDto sessionDto, MovieSession movieSession) {
        MovieSessionTime sessionTime = MovieSessionTime.valueOf(sessionDto.getSessionTime().toUpperCase());
        movieSession.setDate(sessionDto.getDate().withHour(sessionTime.getHourOfDay()));
        movieSession.setSessionTime(sessionTime);
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());
    }

    public static MovieSessionDto toDto(MovieSession movieSession) {
        var movieSessionDto = new MovieSessionDto();
        movieSessionDto.setDate(movieSession.getDate());
        movieSessionDto.setSessionTime(movieSession.getSessionTime().toString());
        movieSessionDto.setPrice(movieSession.getPrice());
        movieSessionDto.setTicketsLeft(movieSession.getTicketsLeft());
        movieSessionDto.setMovieId(movieSession.getMovie().getId());
        movieSessionDto.setHallId(movieSession.getHall().getId());
        return movieSessionDto;
    }
}
