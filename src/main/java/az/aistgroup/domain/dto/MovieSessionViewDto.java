package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieSessionTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MovieSessionViewDto {
    LocalDateTime getDate();

    MovieSessionTime getSessionTime();

    BigDecimal getPrice();

    int getTicketsLeft();

    String getMovieName();

    String getHall();
}
