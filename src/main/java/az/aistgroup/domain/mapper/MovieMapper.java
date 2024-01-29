package az.aistgroup.domain.mapper;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.entity.Movie;
import az.aistgroup.domain.enumeration.MovieGenre;

public final class MovieMapper {

    private MovieMapper() {
    }

    public static Movie toEntity(MovieDto movieDto) {
        var movie = new Movie();
        movie.setName(movieDto.getName());
        movie.setGenre(MovieGenre.valueOf(movieDto.getGenre().toUpperCase()));
        movie.setImdbRating(movieDto.getImdbRating());
        movie.setReleaseYear(movieDto.getReleaseYear());
        return movie;
    }

    public static void toEntityInPlace(MovieDto movieDto, Movie movie) {
        movie.setName(movieDto.getName());
        movie.setGenre(MovieGenre.valueOf(movieDto.getGenre().toUpperCase()));
        movie.setImdbRating(movieDto.getImdbRating());
        movie.setReleaseYear(movieDto.getReleaseYear());
    }

    public static MovieDto toDto(Movie movie) {
        var movieDto = new MovieDto();
        movieDto.setName(movie.getName());
        movieDto.setGenre(movie.getGenre().toString().toUpperCase());
        movieDto.setImdbRating(movie.getImdbRating());
        movieDto.setReleaseYear(movie.getReleaseYear());
        return movieDto;
    }

}
