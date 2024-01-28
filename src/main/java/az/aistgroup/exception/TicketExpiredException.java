package az.aistgroup.exception;

/**
 * The exception will be thrown when {@link az.aistgroup.domain.entity.User} tries to refund, but
 * {@link az.aistgroup.domain.entity.MovieSession} is already closed or fewer hours left for session to begin.
 */
public class TicketExpiredException extends RuntimeException {
    public TicketExpiredException(Long ticketId) {
        super("Ticket " + ticketId + " is expired.");
    }

    public TicketExpiredException(String message) {
        super(message);
    }
}
