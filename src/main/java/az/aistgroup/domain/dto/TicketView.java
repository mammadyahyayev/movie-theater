package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.Ticket;
import org.springframework.hateoas.RepresentationModel;

public class TicketView extends RepresentationModel<TicketView> {
    private Long id;
    private String movie;
    private String session;
    private String ticketHolder;
    private String seat;

    public TicketView() {
    }

    public TicketView(Ticket ticket) {
        this.id = ticket.getId();
        this.movie = ticket.getMovieSession().getMovie().getName();
        this.session = ticket.getMovieSession().getDate() + " " + ticket.getMovieSession().getSessionTime();
        this.ticketHolder = ticket.getUser().getFullName();
        this.seat = ticket.getSeat().getHall().getName() + " Seat: " + ticket.getSeat().getSeatNum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTicketHolder() {
        return ticketHolder;
    }

    public void setTicketHolder(String ticketHolder) {
        this.ticketHolder = ticketHolder;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
