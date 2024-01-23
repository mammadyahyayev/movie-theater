package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieSessionTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionDto {
    @NotNull
    private LocalDateTime date;

    @NotNull
    private MovieSessionTime sessionTime;

    @NotNull
    private BigDecimal price;

    @PositiveOrZero
    private int ticketsLeft;

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
}
