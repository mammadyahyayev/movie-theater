package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.validation.CurrentAndFutureDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionDto {
    @NotNull(message = "{field.notNull}")
    @CurrentAndFutureDate(message = "date can not be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @NotNull(message = "{field.notNull}")
    private MovieSessionTime sessionTime;

    @NotNull(message = "{field.notNull}")
    @Positive(message = "{field.positive}")
    private BigDecimal price;

    @PositiveOrZero(message = "ticketsLeft can not be less than 0")
    private int ticketsLeft;

    @NotNull(message = "{field.notNull}")
    @Positive(message = "{field.positive}")
    private Long movieId;

    @NotNull(message = "{field.notNull}")
    @Positive(message = "{field.positive}")
    private Long hallId;

    public MovieSessionDto() {
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
