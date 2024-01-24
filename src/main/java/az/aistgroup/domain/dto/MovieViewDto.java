package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieGenre;

public interface MovieViewDto {
    String getName();

    MovieGenre getGenre();
}
