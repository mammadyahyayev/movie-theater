package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.validation.CurrentAndFutureDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieSessionDto {
    @NotNull
    @CurrentAndFutureDate
    private LocalDate date;

    @NotNull
    private MovieSessionTime sessionTime;

    @NotNull
    private BigDecimal price;

    @PositiveOrZero
    private int ticketsLeft;

    @Positive
    @NotNull
    private Long movieId;

    @Positive
    @NotNull
    private Long hallId;

    public MovieSessionDto() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MovieSessionTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(MovieSessionTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTicketsLeft() {
        return ticketsLeft;
    }

    public void setTicketsLeft(int ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }
}
