package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.Ticket;

//TODO: Maybe change this to TicketView (and also consider removing Dto suffix from Views)
public class TicketDto {
    private String movie;
    private String session;
    private String ticketHolder;
    private String seat;

    public TicketDto() {
    }

    public TicketDto(Ticket ticket) {
        this.movie = ticket.getMovieSession().getMovie().getName();
        this.session = ticket.getMovieSession().getDate() + " " + ticket.getMovieSession().getSessionTime();
        this.ticketHolder = ticket.getUser().getFullName();
        this.seat = ticket.getSeat().getHall().getName() + " Seat: " + ticket.getSeat().getSeatNum();
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
