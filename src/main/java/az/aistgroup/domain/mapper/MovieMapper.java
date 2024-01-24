package az.aistgroup.domain.mapper;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.entity.Movie;

public final class MovieMapper {

    private MovieMapper() {
    }

    public static Movie toEntity(final MovieDto movieDto) {
        var movie = new Movie();
        movie.setName(movieDto.getName());
        movie.setGenre(movieDto.getGenre());
        return movie;
    }

    public static MovieDto toDto(final Movie movie) {
        var movieDto = new MovieDto();
        movieDto.setName(movie.getName());
        movieDto.setGenre(movie.getGenre());
        return movieDto;
    }

}
