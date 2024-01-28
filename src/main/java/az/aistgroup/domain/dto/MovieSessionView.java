package az.aistgroup.domain.dto;

import az.aistgroup.domain.enumeration.MovieSessionTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionView {
    private LocalDateTime date;
    private MovieSessionTime sessionTime;
    private BigDecimal price;
    private int ticketsLeft;
    private String movieName;
    private String hall;

    public MovieSessionView() {
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

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }
}
