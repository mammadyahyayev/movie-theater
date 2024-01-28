package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.Movie;
import jakarta.validation.constraints.NotBlank;

public class MovieDto {
    @NotBlank(message = "{field.notBlank}")
    private String name;

    @NotBlank(message = "{field.notBlank}")
    private String genre;

    public MovieDto() {
    }

    public MovieDto(Movie movie) {
        this.name = movie.getName();
        this.genre = movie.getGenre().toString().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

