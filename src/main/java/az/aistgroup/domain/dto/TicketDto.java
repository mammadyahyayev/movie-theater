package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.Ticket;
import org.springframework.hateoas.RepresentationModel;

public class TicketDto extends RepresentationModel<TicketDto> {
    private Long id;
    private String movie;
    private String session;
    private String ticketHolder;
    private String seat;

    public TicketDto() {
    }

    public TicketDto(Ticket ticket) {
        this.id = ticket.getId();
        this.movie = ticket.getMovieName();
        this.session = ticket.getMovieSessionTime();
        this.ticketHolder = ticket.getTicketHolder();
        this.seat = ticket.getSeatName();
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
