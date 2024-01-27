package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MovieDto {
    @NotBlank(message = "{field.notBlank}")
    private String name;

    @NotNull(message = "{field.notNull}")
    private MovieGenre genre;

    public MovieDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }
}

