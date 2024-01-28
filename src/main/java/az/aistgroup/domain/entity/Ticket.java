package az.aistgroup.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_generator")
    @SequenceGenerator(name = "ticket_generator", sequenceName = "ticket_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MovieSession movieSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public String getSeatName() {
        return this.getSeat().getHall().getName() + " Seat: " + this.getSeat().getSeatNum();
    }

    public String getTicketHolder() {
        return this.getUser().getFullName();
    }

    public String getMovieSessionTime() {
        return this.getMovieSession().getDate() + " " + this.getMovieSession().getSessionTime();
    }

    public String getMovieName() {
        return this.getMovieSession().getMovie().getName();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MovieSession getMovieSession() {
        return movieSession;
    }

    public void setMovieSession(MovieSession movieSession) {
        this.movieSession = movieSession;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
