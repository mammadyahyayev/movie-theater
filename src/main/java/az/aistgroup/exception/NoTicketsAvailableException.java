package az.aistgroup.exception;

/**
 * The exception will be thrown when there are no {@link az.aistgroup.domain.entity.Ticket}
 * left for {@link az.aistgroup.domain.entity.MovieSession} to buy.
 */
public class NoTicketsAvailableException extends RuntimeException {
    public NoTicketsAvailableException() {
        super("No tickets available for purchase!");
    }
}
