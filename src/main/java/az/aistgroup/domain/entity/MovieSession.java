package az.aistgroup.domain.entity;

import az.aistgroup.domain.enumeration.MovieSessionTime;
import az.aistgroup.exception.NoTicketsAvailableException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_sessions")
public class MovieSession extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_generator")
    @SequenceGenerator(name = "session_generator", sequenceName = "session_seq", allocationSize = 1)
    private Long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private MovieSessionTime sessionTime;

    private BigDecimal price;

    private int ticketsLeft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    private boolean isClosed;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public void decreaseLeftTickets() {
        if (this.ticketsLeft == 0) {
            throw new NoTicketsAvailableException();
        }

        this.ticketsLeft--;
    }

    public void increaseLeftTickets() {
        if (this.hall.getCapacity() == this.ticketsLeft) {
            throw new IllegalArgumentException("Can not increase left tickets because it exceeds hall capacity!");
        }

        this.ticketsLeft++;
    }
}
