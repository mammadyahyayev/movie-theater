package az.aistgroup.exception;

public class TicketExpiredException extends RuntimeException {
    public TicketExpiredException(Long ticketId) {
        super("Ticket " + ticketId + " is expired.");
    }
}
