package az.aistgroup.exception;

public class NoTicketsAvailableException extends RuntimeException {
    public NoTicketsAvailableException() {
        super("No tickets available for purchase!");
    }
}
