package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovieDto(
        @NotNull
        @NotBlank
        String name,

        @NotNull
        MovieGenre genre
) {
}

