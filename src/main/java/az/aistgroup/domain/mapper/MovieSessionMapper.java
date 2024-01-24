package az.aistgroup.domain.mapper;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;

public final class MovieSessionMapper {

    private MovieSessionMapper() {
    }

    public static MovieSession toEntity(MovieSessionDto sessionDto) {
        var movieSession = new MovieSession();
        movieSession.setDate(sessionDto.getDate());
        movieSession.setSessionTime(sessionDto.getSessionTime());
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());
        return movieSession;
    }

    public static MovieSession toEntityInPlace(MovieSessionDto sessionDto, MovieSession movieSession) {
        movieSession.setDate(sessionDto.getDate());
        movieSession.setSessionTime(sessionDto.getSessionTime());
        movieSession.setPrice(sessionDto.getPrice());
        movieSession.setTicketsLeft(sessionDto.getTicketsLeft());
        return movieSession;
    }

    public static MovieSessionDto toDto(MovieSession movieSession) {
        var movieSessionDto = new MovieSessionDto();
        movieSessionDto.setDate(movieSession.getDate());
        movieSessionDto.setSessionTime(movieSession.getSessionTime());
        movieSessionDto.setPrice(movieSession.getPrice());
        movieSessionDto.setTicketsLeft(movieSession.getTicketsLeft());
        return movieSessionDto;
    }
}
