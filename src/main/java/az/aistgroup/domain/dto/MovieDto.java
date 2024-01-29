package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.Movie;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class MovieDto {
    @NotBlank(message = "{field.notBlank}")
    private String name;

    @NotBlank(message = "{field.notBlank}")
    private String genre;

    @Positive(message = "{field.positive}")
    private int releaseYear;

    @Max(value = 10, message = "IMDB rating cannot be more than 10.")
    @Min(value = 0, message = "IMDB rating cannot be less than 0.")
    @Positive(message = "{field.positive}")
    private double imdbRating;

    public MovieDto() {
    }

    public MovieDto(Movie movie) {
        this.name = movie.getName();
        this.genre = movie.getGenre().toString().toUpperCase();
        this.imdbRating = movie.getImdbRating();
        this.releaseYear = movie.getReleaseYear();
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

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }
}

